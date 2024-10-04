package com.example.tracker.controller;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.service.interfaces.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reminder")
public class ReminderController {
    private final ReminderService reminderService;

    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody ReminderDTO reminderDTO){
        return ResponseEntity.ok(this.reminderService.save(reminderDTO));
    }

    @PutMapping(value = "/")
    public ResponseEntity<ReminderDTO> updateReminder(@RequestBody ReminderDTO reminderDTO){
        return ResponseEntity.ok(this.reminderService.update(reminderDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id){
        this.reminderService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReminderDTO> getReminder(@PathVariable Long id){
        return ResponseEntity.ok(this.reminderService.findById(id));
    }
}
