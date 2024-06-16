package com.example.application.repositories;

import com.example.application.entities.AssetWatcher;
import com.example.application.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

    List<AssetWatcher> findByUser(User user);

}
