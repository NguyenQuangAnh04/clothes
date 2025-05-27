package com.example.clothes.controller;

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

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody() UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PutMapping("/update")
    public ResponseEntity<User> update(@RequestBody() UserRegisterDTO dto){
        return ResponseEntity.ok(userService.updateUser(dto));
    }
}
