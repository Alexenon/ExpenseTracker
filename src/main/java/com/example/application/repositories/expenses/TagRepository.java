package com.example.application.repositories.expenses;

import com.example.application.entities.expenses.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query(value = """
			         SELECT * FROM tags T
			         WHERE T.name = :name
			""", nativeQuery = true)
	Optional<Tag> findByName(@Param("name") String name);

	@Query(value = """
			         SELECT * FROM tags T
			         WHERE T.user_id = :userId
			""", nativeQuery = true)
	List<Tag> findByUser(@Param("userId") long userId);

	@Query(value = """
			         SELECT * FROM tags T
			         WHERE T.name = :name AND T.user_id = :userId
			""", nativeQuery = true)
	Optional<Tag> findByNameAndUser(@Param("name") String name, @Param("userId") long userId);

}
