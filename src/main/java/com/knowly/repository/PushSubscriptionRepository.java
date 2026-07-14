package com.knowly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowly.entity.PushSubscription;
import com.knowly.entity.UserProfile;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, String> {

	List<PushSubscription> findByUserProfile(UserProfile userProfile);

	Optional<PushSubscription> findByEndpoint(String endpoint);

	void deleteByEndpoint(String endpoint);
}
