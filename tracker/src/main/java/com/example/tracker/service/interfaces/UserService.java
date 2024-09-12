package com.example.tracker.service.interfaces;

import com.example.tracker.dto.UserDTO;
import com.example.tracker.model.User;

public interface UserService extends CrudService<UserDTO, Long> {
    User findEntityById(Long id);

}
