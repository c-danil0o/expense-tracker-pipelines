package com.example.tracker.mapper;

import com.example.tracker.dto.UserDTO;
import com.example.tracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder().userId(user.getUserId()).type(user.getType()).funds(user.getFunds()).
                reservedFunds(user.getReservedFunds()).email(user.getEmail()).currency(user.getCurrency()).
                firstName(user.getFirstName()).lastName(user.getLastName()).country(user.getCountry()).build();
    }

    public User fromUserDTO(UserDTO userDTO){
        return User.builder().userId(userDTO.getUserId()).firstName(userDTO.getFirstName()).
                lastName(userDTO.getLastName()).email(userDTO.getEmail()).funds(userDTO.getFunds()).
                reservedFunds(userDTO.getReservedFunds()).currency(userDTO.getCurrency()).
                type(userDTO.getType()).password(userDTO.getPassword()).country(userDTO.getCountry()).build();
    }
}
