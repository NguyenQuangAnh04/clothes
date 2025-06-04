package com.example.clothes.service;

import com.example.clothes.enums.OTPPurpose;

public interface IOTPService {
    void sendOTP(String email, OTPPurpose otpPurpose);
    boolean verifyOTP(String email, String OTP);
}
