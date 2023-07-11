package com.example.application.controllers;

import com.example.application.entities.Timestamp;
import com.example.application.repositories.TimestampRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timestamp")
public class TimestampController {

    private final TimestampRepository repository;

    public TimestampController(TimestampRepository repository) {
        this.repository = repository;
    }

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
