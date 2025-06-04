package com.example.clothes.service;

import com.example.clothes.component.JwtUtils;
import com.example.clothes.dto.ChangePasswordDTO;
import com.example.clothes.dto.UserRegisterDTO;
import com.example.clothes.enums.OTPPurpose;
import com.example.clothes.model.Role;
import com.example.clothes.model.User;
import com.example.clothes.repository.RoleRepository;
import com.example.clothes.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private OTPService otpService;
    private final Map<String, UserRegisterDTO> tempRegisterCache = new ConcurrentHashMap<>(); // Map này sẽ an toàn khi có nhiều luồng truy cập đồng thời
    @Override
    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUserNameWithRoles(username);
        if (!user.isPresent()) throw new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu!");
        if (!bCryptPasswordEncoder.matches(password, user.get().getPassword()))
            throw new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu!");
        return jwtUtils.generateToken(user.get());
    }

    @Override
    public void requestRegister(UserRegisterDTO userRegisterDTO) {
        if (userRepository.findByUserName(userRegisterDTO.getUserName()).isPresent())
            throw new RuntimeException("Tài khoản đã tồn tại");
        if (userRepository.findByEmail(userRegisterDTO.getEmail()).isPresent())
            throw new RuntimeException("Email đã tồn tại");
        if (userRepository.findByPhone(userRegisterDTO.getPhone()).isPresent())
            throw new RuntimeException("SĐT đã tồn tại");
        if (userRegisterDTO.getPassword().length() < 8)
            throw new RuntimeException("Mật khẩu phải có 8 kí tự!");
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu không khớp");
        if (!userRegisterDTO.getPhone().matches("^(0[35789])[0-9]{8}$"))
            throw new RuntimeException("SĐT không đúng định dạng");
        if (!containUpperCaseCharacter(userRegisterDTO.getPassword()))
            throw new RuntimeException("Mật khẩu phải ít nhất 1 chữ cái viết hoa");
        if (!containSpecialCharacter(userRegisterDTO.getPassword()))
            throw new RuntimeException("Mật khẩu phải có ít nhất 1 kí tự đặc biệt");
        otpService.sendOTP(userRegisterDTO.getEmail(), OTPPurpose.FOR_REGISTER);
        tempRegisterCache.put(userRegisterDTO.getEmail(), userRegisterDTO);
    }

    @Override
    public User verifyOtpAndRegister(String email, String otp) {
        UserRegisterDTO userRegisterDTO = tempRegisterCache.get(email);
        if (userRegisterDTO == null)
            throw new RuntimeException("Thông tin đăng ký đã hết hạn hoặc không tồn tại");
        if (!otpService.verifyOTP(email, otp))
            throw new RuntimeException("OTP không chính xác hoặc đã hết hạn");
        String hashPassword = bCryptPasswordEncoder.encode(userRegisterDTO.getPassword());
        User newUser = new User();
        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
        newUser.setUserName(userRegisterDTO.getUserName());
        newUser.setPassword(hashPassword);
        newUser.setFullName(userRegisterDTO.getFullName());
        newUser.setPhone(userRegisterDTO.getPhone());
        newUser.setEmail(userRegisterDTO.getEmail());
        newUser.setCreated_at(LocalDateTime.now());
        newUser.setRoles(role);
        tempRegisterCache.remove(email);
        return userRepository.save(newUser);

    }

    @Override
    public void resetPassword(String email, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        if (!otpService.verifyOTP(email, changePasswordDTO.getOtp()))
            throw new RuntimeException("OTP không đúng hoặc đã hết hạn");
        if (changePasswordDTO.getPassword().length() < 8) throw new RuntimeException("Mật khẩu phải có 8 kí tự");
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu không giống nhau");
        if (!containUpperCaseCharacter(changePasswordDTO.getPassword()))
            throw new RuntimeException("Mật khẩu phải ít nhất 1 chữ cái viết hoa");
        if (!containSpecialCharacter(changePasswordDTO.getPassword()))
            throw new RuntimeException("Mật khẩu phải có ít nhất 1 kí tự đặc biệt");
        String hashPassword = bCryptPasswordEncoder.encode(changePasswordDTO.getPassword());
        user.setPassword(hashPassword);
        userRepository.save(user);
        log.info("Đổi mật khẩu thành công");

    }


    private boolean containUpperCaseCharacter(String password) {
        String uppercasePattern = ".*[A-Z].*";
        return password.matches(uppercasePattern);
    }

    private boolean containSpecialCharacter(String password) {
        String specialCharPattern = ".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-].*";
        return password.matches(specialCharPattern);
    }

    @Override
    public User updateUser(UserRegisterDTO userRegisterDTO) {
        User user = userRepository.findById(userRegisterDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản!"));
        if (!user.getEmail().equals(userRegisterDTO.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(userRegisterDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
            }
        }
        user.setFullName(userRegisterDTO.getFullName());
        user.setPhone(userRegisterDTO.getPhone());
        user.setEmail(userRegisterDTO.getEmail());
        String newPassword = userRegisterDTO.getPassword();
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String confirmPassword = userRegisterDTO.getConfirmPassword();
            if (newPassword.length() < 8) {
                throw new RuntimeException("Mật khẩu phải có ít nhất 8 ký tự");
            }
            if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp");
            }
            String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    @Override
    public void sendForgotPasswordOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        otpService.sendOTP(user.getEmail(), OTPPurpose.FORGOT_PASSWORD);
        log.info("Đã gửi mã OTP đến email bạn vui lòng kiểm trong thư rác");

    }


}
