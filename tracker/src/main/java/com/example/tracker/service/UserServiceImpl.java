package com.example.tracker.service;

import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User findById(Long userId) throws ElementNotFoundException {
        return this.userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("No such element with given id!"));
    }

    @Override
    public User save(User object) {
        return this.userRepository.save(object);
    }

    @Override
    public User update(User newUser) throws ElementNotFoundException {
        User user = this.userRepository.findById(newUser.getUserId()).orElseThrow(() -> new ElementNotFoundException("No such element with given id!"));
        user.setCurrency(newUser.getCurrency());
        user.setEmail(newUser.getEmail());
        user.setFunds(newUser.getFunds());
        user.setType(newUser.getType());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setReservedFunds(newUser.getReservedFunds());

        return this.userRepository.save(user);
    }

    @Override
    public void delete(Long userId) throws ElementNotFoundException {
        if (this.userRepository.existsById(userId)){
            this.userRepository.deleteById(userId);
        }else{
            throw new ElementNotFoundException("No such element with given id!");
        }

    }
}
