package com.example.application.controller;

import com.example.application.model.Timestamp;
import com.example.application.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timestamp")
public class TimestampController {

    @Autowired
    TimestampRepository repository;

    @GetMapping("/all")
    public List<Timestamp> getAllTimestamps() {
        return repository.findAll();
    }

    @GetMapping("/add")
    public String addTimestamp(Timestamp timestamp) {
        repository.save(timestamp);
        return "Timestamp was added succesfully";
    }

    @GetMapping("/delete/{id}")
    public String deleteTimestamp(@PathVariable Long id) {
        repository.deleteById(id);
        return "Timestamp was added succesfully";
    }
}
