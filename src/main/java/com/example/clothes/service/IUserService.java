package com.example.clothes.service;

import com.example.clothes.dto.UserRegisterDTO;
import com.example.clothes.model.User;

public interface IUserService {
    String login(String username, String password);
    User register(UserRegisterDTO userRegisterDTO);

    User updateUser(UserRegisterDTO userRegisterDTO);
}
