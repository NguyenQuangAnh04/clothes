package com.example.clothes.service;

import com.example.clothes.dto.ChangePasswordDTO;
import com.example.clothes.dto.UserRegisterDTO;
import com.example.clothes.model.User;

public interface IUserService {
    String login(String username, String password);

    User verifyOtpAndRegister(String email, String otp);

    void resetPassword(String email, ChangePasswordDTO changePasswordDTO);

    User updateUser(UserRegisterDTO userRegisterDTO);
    void requestRegister(UserRegisterDTO userRegisterDTO);
    void sendForgotPasswordOTP(String email);
}
