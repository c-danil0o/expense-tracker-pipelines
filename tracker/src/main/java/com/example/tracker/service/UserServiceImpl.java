package com.example.tracker.service;

import com.example.tracker.dto.LoginDTO;
import com.example.tracker.dto.UserDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.UserRegistrationException;
import com.example.tracker.mapper.UserMapper;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.EventStreamService;
import com.example.tracker.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EventStreamService eventStreamService;

    @Override
    public List<UserDTO> findAll() {
        return this.userRepository.findAll().stream().map(this.userMapper::toUserDTO).toList();
    }

    @Override
    public UserDTO findById(Long userId) throws ElementNotFoundException {
        return this.userMapper.toUserDTO(this.userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("No such element with given id!")));
    }

    @Override
    public UserDTO save(UserDTO userDTO) {
        User savedUser = this.userRepository.save(this.userMapper.fromUserDTO(userDTO));
        return this.userMapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO update(UserDTO newUser) throws ElementNotFoundException {
        if (!this.userRepository.existsById(newUser.getUserId()))
            throw new ElementNotFoundException("User with given id doesn't exist!");
        User user = this.userMapper.fromUserDTO(newUser);
        user.setUserId(newUser.getUserId());
        User savedUser = this.userRepository.save(user);
        return this.userMapper.toUserDTO(savedUser);
    }

    @Override
    public void delete(Long userId) throws ElementNotFoundException {
        if (this.userRepository.existsById(userId)){
            this.userRepository.deleteById(userId);
        }else{
            throw new ElementNotFoundException("No such element with given id!");
        }

    }

    @Override
    public User findEntityById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("No such element with given id!"));
    }

    @Transactional
    @Override
    public UserDTO register(UserDTO userDTO) {
        User account = this.userRepository.findByEmail(userDTO.getEmail()).orElse(null);
        if (account != null)
           throw new UserRegistrationException("User with given email already exists!");
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        this.eventStreamService.sendRecord(LocalDateTime.now(), "User_Register_EXECUTED", "user", null, "BASIC");
        return this.userMapper.toUserDTO(this.userRepository.save(this.userMapper.fromUserDTO(userDTO)));
    }

    @Transactional
    @Override
    public User login(LoginDTO loginDTO) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        this.eventStreamService.sendRecord(LocalDateTime.now(), "User_Login_EXECUTED", "user", null, "BASIC");

        return this.userRepository.findByEmail(loginDTO.getEmail()).orElseThrow();
    }
}
