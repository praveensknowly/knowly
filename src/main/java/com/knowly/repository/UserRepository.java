package com.knowly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findByNumber(String number);
    public boolean existsByEmail(String email);
    public boolean existsByNumber(String number);
}
