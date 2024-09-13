package com.example.tracker.controller;

import com.example.tracker.dto.UserDTO;
import com.example.tracker.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(this.userService.save(userDTO));
    }

    @GetMapping(value = "/user/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(this.userService.findAll());
    }


    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.findById(userId));
    }

    @PutMapping(value = "/user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(this.userService.update(userDTO));
    }

    @DeleteMapping(value = "/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        this.userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
