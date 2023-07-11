package com.example.application.services;

import com.example.application.entities.Timestamp;
import com.example.application.repositories.TimestampRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class TimestampService {

    @Autowired
    TimestampRepository repository;

    public List<Timestamp> getAllTimestamps() {
        return repository.findAll();
    }

    public Timestamp getTimestampByName(String name) {
        return repository.getByName(name);
    }

}
