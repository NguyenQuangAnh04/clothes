package com.example.clothes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;


}
