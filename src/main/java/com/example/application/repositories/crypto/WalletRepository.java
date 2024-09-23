package com.example.application.repositories.crypto;

import com.example.application.entities.User;
import com.example.application.entities.crypto.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUser(User user);

}
