package com.example.application.service;

import com.example.application.model.Timestamp;
import com.example.application.repository.TimestampRepository;
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
