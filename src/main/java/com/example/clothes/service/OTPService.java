package com.example.clothes.service;

import com.example.clothes.dto.OTPDTO;
import com.example.clothes.enums.OTPPurpose;
import com.example.clothes.model.User;
import com.example.clothes.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService implements IOTPService {
    private final  Map<String, OTPDTO> map = new HashMap<>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private String randomOTP() {
        Random random = new Random();
        String otp = String.format("%6d", random.nextInt(999999));
        return otp;
    }



    @Override
    public void sendOTP(String email, OTPPurpose otpPurpose) {

        String otp = randomOTP();
        OTPDTO otp1 = new OTPDTO(otp);
        map.put(email, otp1);
        String subject;
        String body;
        // Nội dung email
        if (otpPurpose == OTPPurpose.FOR_REGISTER) {
            subject = "Mã OTP Xác Thực Đăng Ký Tài Khoản - Shop-App";
            body = "Xin chào,\n\n" +
                    "Cảm ơn bạn đã đăng ký tài khoản tại Shop-App.\n" +
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                    "Mã này có hiệu lực trong 5 phút.\n\n" +
                    "Trân trọng,\nShop-App Team";

        } else {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy email"));
            subject = "Mã OTP Xác Thực Đổi Mật Khẩu Của Bạn - Shop-App";
            body = "Xin chào,\n\n" +
                    "Chúng tôi nhận thấy có yêu cầu thay đổi mật khẩu từ tài khoản của bạn.\n" +
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                    "Mã này có hiệu lực trong 5 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n" +
                    "Trân trọng,\nShop-App Team";
        }
        try {
            EmailSender.sendEmail(email, subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyOTP(String email, String OTP) {
        OTPDTO otp = map.get(email);
        if (otp == null) return true;
        if (otp.isExpired()) {
            map.remove(email);
            return false;
        }
        if (otp.getOtp().equals(OTP)) {
            map.remove(email);
            return true;
        }
        return false;
    }
}
