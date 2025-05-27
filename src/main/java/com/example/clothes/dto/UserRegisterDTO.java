package com.example.clothes.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class UserRegisterDTO {
    private Long id;
    private String userName;
    private String fullName;
    private String password;
    private String confirmPassword;
    private String email;
    private String phone;

}
