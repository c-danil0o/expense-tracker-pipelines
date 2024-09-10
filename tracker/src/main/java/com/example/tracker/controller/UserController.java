package com.example.tracker.controller;

import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.model.User;
import com.example.tracker.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        this.userService.save(user);
        return ResponseEntity.ok(user);

    }

    @GetMapping(value = "/user/all" )
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(this.userService.findAll());
    }


    @GetMapping(value = "/user/{userId}" )
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.findById(userId));
    }

    @PutMapping(value = "/user")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(this.userService.update(user));
    }

    @DeleteMapping(value = "/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        this.userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
