/* Knowly — Web Push subscription handling */
(function () {
	'use strict';

	function getCookie(name) {
		var value = '; ' + document.cookie;
		var parts = value.split('; ' + name + '=');
		if (parts.length === 2) return parts.pop().split(';').shift();
	}

	function urlBase64ToUint8Array(base64String) {
		var padding = '='.repeat((4 - (base64String.length % 4)) % 4);
		var base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
		var rawData = window.atob(base64);
		var outputArray = new Uint8Array(rawData.length);
		for (var i = 0; i < rawData.length; ++i) {
			outputArray[i] = rawData.charCodeAt(i);
		}
		return outputArray;
	}

	function postJson(url, body) {
		var csrfToken = getCookie('XSRF-TOKEN');
		var headers = { 'Content-Type': 'application/json' };
		if (csrfToken) headers['X-XSRF-TOKEN'] = csrfToken;
		return fetch(url, {
			method: 'POST',
			headers: headers,
			body: JSON.stringify(body)
		});
	}

	async function isSubscribed() {
		if (!('serviceWorker' in navigator) || !('PushManager' in window)) return false;
		var reg = await navigator.serviceWorker.getRegistration();
		if (!reg) return false;
		var sub = await reg.pushManager.getSubscription();
		return !!sub;
	}

	async function subscribe() {
		if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
			throw new Error('Push notifications are not supported in this browser.');
		}

		var permission = await Notification.requestPermission();
		if (permission !== 'granted') {
			throw new Error('Notification permission was not granted.');
		}

		var reg = await navigator.serviceWorker.register('/sw.js');
		await navigator.serviceWorker.ready;

		var keyResponse = await fetch('/push/vapid-public-key');
		var keyData = await keyResponse.json();
		if (!keyData.publicKey) {
			throw new Error('Push notifications are not configured on the server.');
		}

		var subscription = await reg.pushManager.subscribe({
			userVisibleOnly: true,
			applicationServerKey: urlBase64ToUint8Array(keyData.publicKey)
		});

		var response = await postJson('/push/subscribe', subscription.toJSON());
		if (!response.ok) {
			throw new Error('Failed to save subscription on the server.');
		}
		return subscription;
	}

	async function unsubscribe() {
		if (!('serviceWorker' in navigator)) return;
		var reg = await navigator.serviceWorker.getRegistration();
		if (!reg) return;
		var sub = await reg.pushManager.getSubscription();
		if (!sub) return;

		var endpoint = sub.endpoint;
		await sub.unsubscribe();
		await postJson('/push/unsubscribe', { endpoint: endpoint });
	}

	window.KnowlyPush = {
		isSubscribed: isSubscribed,
		subscribe: subscribe,
		unsubscribe: unsubscribe
	};
})();
