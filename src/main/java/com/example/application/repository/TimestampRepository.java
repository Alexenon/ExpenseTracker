package com.example.application.repository;

import com.example.application.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
}
