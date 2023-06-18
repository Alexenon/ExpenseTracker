package com.example.application.repository;

import com.example.application.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            SELECT * FROM category
            WHERE name = :name
            LIMIT 1
            """, nativeQuery = true)
    Category getByName(@Param("name") String name);
}