package com.example.application.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(value = """
			SELECT * FROM categories
			WHERE name = :name
			""", nativeQuery = true)
	Optional<Category> findByName(@Param("name") String name);

	@Query(value = """
			SELECT * FROM categories
			WHERE user_id = :userId
			""", nativeQuery = true)
	List<Category> findByUser(@Param("userId") Long userId);

	@Query(value = """
			SELECT * FROM categories
			WHERE name = :name AND user_id = :userId
			""", nativeQuery = true)
	Optional<Category> findByNameAndUser(
			@Param("name") String name,
			@Param("userId") Long userId
	);

	@Query(value = """
			SELECT * FROM categories
			WHERE icon_name = :iconName AND user_id = :userId
			""", nativeQuery = true)
	Optional<Category> findByIconAndUser(
			@Param("iconName") String iconName,
			@Param("userId") Long userId
	);


}