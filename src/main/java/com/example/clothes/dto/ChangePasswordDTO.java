package com.example.clothes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String email;
    @NotBlank()
    private String otp;
    @NotBlank(message = "Không được bỏ trống mật khẩu")
    private String password;
    @NotBlank(message = "Không được bỏ trống mật khẩu")

    private String confirmPassword;
}
