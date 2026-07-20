(function () {
  const sessionId = document.currentScript.getAttribute('data-session-id');
  let stompClient, peerConnection, localStream, currentCallId, callTimerInterval;
  let isCaller = false;
  let pendingCandidates = [];

  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.debug = null;

  stompClient.connect({}, () => {
    stompClient.subscribe('/user/queue/signal', msg => handleSignal(JSON.parse(msg.body)));
  }, (error) => {
    console.error('WebSocket/STOMP connection failed:', error);
  });

  document.getElementById('startCallBtn').addEventListener('click', () => {
    isCaller = true;
    document.getElementById('startCallBtn').disabled = true;
    document.getElementById('startCallBtn').textContent = '📞 Calling…';
    stompClient.send('/app/call/start', {}, JSON.stringify({ sessionId }));
  });

  function resetCallButton() {
    const btn = document.getElementById('startCallBtn');
    btn.disabled = false;
    btn.textContent = '📞 Call';
  }

  async function handleSignal(msg) {
    switch (msg.type) {
      case 'incoming-call':
        currentCallId = msg.callId;
        isCaller = false;
        document.getElementById('incomingCallText').textContent = msg.fromName + ' is calling...';
        document.getElementById('incomingCallModal').style.display = 'flex';
        break;
      case 'callee-ready':
        // Caler receives this when callee is ready - now create and send offer
        resetCallButton();
        await setupPeerConnection();
        const offer = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offer);
        stompClient.send('/app/call/offer', {}, JSON.stringify({ callId: currentCallId, sdp: offer }));
        break;
      case 'offer':
        currentCallId = msg.callId;
        await setupPeerConnection();
        await peerConnection.setRemoteDescription(new RTCSessionDescription(msg.sdp));
        for (const c of pendingCandidates) await peerConnection.addIceCandidate(new RTCIceCandidate(c));
        pendingCandidates = [];
        const answer = await peerConnection.createAnswer();
        await peerConnection.setLocalDescription(answer);
        stompClient.send('/app/call/answer', {}, JSON.stringify({ callId: currentCallId, sdp: answer }));
        break;
      case 'answer':
        await peerConnection.setRemoteDescription(new RTCSessionDescription(msg.sdp));
        for (const c of pendingCandidates) await peerConnection.addIceCandidate(new RTCIceCandidate(c));
        pendingCandidates = [];
        break;
      case 'ice-candidate':
        if (msg.candidate) {
          if (peerConnection && peerConnection.remoteDescription) {
            await peerConnection.addIceCandidate(new RTCIceCandidate(msg.candidate));
          } else {
            pendingCandidates.push(msg.candidate);
          }
        }
        break;
      case 'call-ended':
        endCallLocal();
        break;
    }
  }

  document.getElementById('acceptCallBtn').addEventListener('click', async () => {
    document.getElementById('incomingCallModal').style.display = 'none';
    // Signal to caller that we're ready to receive the offer
    stompClient.send('/app/call/ready', {}, JSON.stringify({ callId: currentCallId }));
  });

  document.getElementById('rejectCallBtn').addEventListener('click', () => {
    document.getElementById('incomingCallModal').style.display = 'none';
    stompClient.send('/app/call/end', {}, JSON.stringify({ callId: currentCallId }));
  });

  document.getElementById('hangupBtn').addEventListener('click', () => {
    stompClient.send('/app/call/end', {}, JSON.stringify({ callId: currentCallId }));
    endCallLocal();
  });

  document.getElementById('muteBtn').addEventListener('click', () => {
    if (localStream) {
      const audioTrack = localStream.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        document.getElementById('muteBtn').textContent = audioTrack.enabled ? 'Mute' : 'Unmute';
      }
    }
  });

  async function setupPeerConnection() {
    const iceRes = await fetch('/api/turn-credentials');
    const turn = await iceRes.json();

    const iceServers = [
      { urls: 'stun:stun.l.google.com:19302' }
    ];

    if (turn.urls && turn.urls.length > 0 && turn.username && turn.credential) {
      turn.urls.forEach(u => iceServers.push({ urls: u, username: turn.username, credential: turn.credential }));
    }

    peerConnection = new RTCPeerConnection({ iceServers });

    localStream = await navigator.mediaDevices.getUserMedia({ audio: true });
    localStream.getTracks().forEach(t => peerConnection.addTrack(t, localStream));

    peerConnection.ontrack = e => {
      document.getElementById('remoteAudio').srcObject = e.streams[0];
    };

    peerConnection.onicecandidate = e => {
      if (e.candidate) {
        stompClient.send('/app/call/ice-candidate', {}, JSON.stringify({ callId: currentCallId, candidate: e.candidate }));
      }
    };

    peerConnection.onconnectionstatechange = () => {
      if (peerConnection.connectionState === 'connected') {
        stompClient.send('/app/call/connected', {}, JSON.stringify({ callId: currentCallId }));
        showActiveCallBar();
      } else if (peerConnection.connectionState === 'disconnected' || 
                 peerConnection.connectionState === 'failed' ||
                 peerConnection.connectionState === 'closed') {
        endCallLocal();
      }
    };
  }

  function showActiveCallBar() {
    document.getElementById('activeCallBar').style.display = 'flex';
    let seconds = 0;
    callTimerInterval = setInterval(() => {
      seconds++;
      const m = String(Math.floor(seconds / 60)).padStart(2, '0');
      const s = String(seconds % 60).padStart(2, '0');
      document.getElementById('callTimer').textContent = `${m}:${s}`;
    }, 1000);
  }

  function endCallLocal() {
    resetCallButton();
    pendingCandidates = [];
    if (peerConnection) {
      peerConnection.close();
      peerConnection = null;
    }
    if (localStream) {
      localStream.getTracks().forEach(t => t.stop());
      localStream = null;
    }
    clearInterval(callTimerInterval);
    document.getElementById('activeCallBar').style.display = 'none';
    document.getElementById('callTimer').textContent = '00:00';
    document.getElementById('muteBtn').textContent = 'Mute';
    currentCallId = null;
    isCaller = false;
  }
})();
