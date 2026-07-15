package com.knowly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.PushSubscription;
import com.knowly.entity.UserProfile;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, String> {

	Optional<PushSubscription> findByEndpoint(String endpoint);

	List<PushSubscription> findByUserProfile(UserProfile userProfile);

	void deleteByEndpoint(String endpoint);
}