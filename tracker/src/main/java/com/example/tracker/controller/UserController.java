package com.example.tracker.controller;

import com.example.tracker.dto.LoginDTO;
import com.example.tracker.dto.UserDTO;
import com.example.tracker.model.User;
import com.example.tracker.service.JwtService;
import com.example.tracker.service.interfaces.UserService;
import com.example.tracker.utils.AuthToken;
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
    private final JwtService jwtService;

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

    @PostMapping(value = "/user/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO){
        UserDTO newUser = this.userService.register(userDTO);
        return ResponseEntity.ok(newUser);
    }


    @PostMapping(value = "/user/auth")
    public ResponseEntity<AuthToken> loginUser(@RequestBody LoginDTO loginDTO){
        User authenticatedUser = this.userService.login(loginDTO);
        String jwt = this.jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok(new AuthToken(jwt, this.jwtService.getExpirationTime()));
    }
}
