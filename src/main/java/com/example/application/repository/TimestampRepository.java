package com.example.application.repository;

import com.example.application.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimestampRepository extends JpaRepository<Timestamp, Long> {

    @Query(value = """
            SELECT * FROM timestamp
            WHERE name = :name
            LIMIT 1
            """, nativeQuery = true)
    Timestamp getByName(@Param("name") String name);
}
