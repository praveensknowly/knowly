package com.knowly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.knowly.entity.Call;

public interface CallRepository extends JpaRepository<Call, String> {
}
