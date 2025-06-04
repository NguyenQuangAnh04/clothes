package com.example.clothes.controller;

import com.example.clothes.dto.ChangePasswordDTO;
import com.example.clothes.dto.UserLoginDTO;
import com.example.clothes.dto.UserRegisterDTO;
import com.example.clothes.model.User;
import com.example.clothes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody() UserLoginDTO dto) {
        return ResponseEntity.ok(userService.login(dto.getUserName(), dto.getPassword()));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<User> register(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(userService.verifyOtpAndRegister(email, otp));
    }
    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendOtpRegister(@RequestBody() UserRegisterDTO dto) {
        userService.requestRegister(dto);
        return ResponseEntity.ok("OTP đã được gửi về email. Vui lòng kiểm tra email để xác thực.");
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody() UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> forgotPassword(@RequestParam(name = "email") String email) {
        userService.sendForgotPasswordOTP(email);
        return ResponseEntity.ok("Đã gửi OTP đến email của bạn.");
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody() ChangePasswordDTO changePasswordDTO) {
        userService.resetPassword(changePasswordDTO.getEmail(), changePasswordDTO);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công.");
    }
}
