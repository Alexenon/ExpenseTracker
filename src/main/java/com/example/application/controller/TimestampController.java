package com.example.application.controller;

import com.example.application.model.Timestamp;
import com.example.application.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public String addTimestamp(@RequestBody Timestamp timestamp) {
        repository.save(timestamp);
        return "Timestamp was added succesfully";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTimestamp(@PathVariable Long id) {
        repository.deleteById(id);
        return "Timestamp was deleted succesfully";
    }
}
