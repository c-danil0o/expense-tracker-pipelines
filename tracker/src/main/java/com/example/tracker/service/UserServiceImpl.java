package com.example.tracker.service;

import com.example.tracker.dto.UserDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.mapper.UserMapper;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
