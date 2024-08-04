package com.example.application.repositories;

import com.example.application.entities.User;
import com.example.application.entities.crypto.AssetWatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetWatcherRepository extends JpaRepository<AssetWatcher, Long> {

    List<AssetWatcher> findByUser(User user);

}
