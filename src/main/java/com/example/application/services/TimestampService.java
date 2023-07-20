package com.example.application.services;

import com.example.application.dtos.Timestamps;
import com.example.application.entities.Timestamp;
import com.example.application.repositories.TimestampRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimestampService {

    private final TimestampRepository repository;

    public TimestampService(TimestampRepository repository) {
        this.repository = repository;
    }

    public List<Timestamp> getAllTimestamps() {
        return repository.findAll();
    }

    public Timestamp getTimestampByName(String name) {
        return repository.getByName(name);
    }

    public Timestamp getTimestamp(Timestamps timestamp) {
        String name = timestamp.name().replace("_", " ");
        return repository.getByName(name);
    }

}
