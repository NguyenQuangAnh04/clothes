package com.example.clothes.service;

import com.example.clothes.component.JwtUtils;
import com.example.clothes.dto.UserRegisterDTO;
import com.example.clothes.model.Role;
import com.example.clothes.model.User;
import com.example.clothes.repository.RoleRepository;
import com.example.clothes.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUserNameWithRoles(username);
        if (!user.isPresent()) throw new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu!");
        if (!bCryptPasswordEncoder.matches(password, user.get().getPassword()))
            throw new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu!");
        return jwtUtils.generateToken(user.get());
    }

    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        Optional<User> user = userRepository.findByUserName(userRegisterDTO.getUserName());
        if (user.isPresent()) throw new RuntimeException("Tài khoản đã đăng ký vui lòng nhập tài khoản khác!");
        if (userRegisterDTO.getPassword().length() < 8 || userRegisterDTO.getPassword().isEmpty())
            throw new RuntimeException("Mật khẩu không được bỏ trống và mật khẩu phải trên 8 kí tự!");
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu không khớp");
        if (!userRegisterDTO.getPhone().matches("^(0[35789])[0-9]{8}$"))
            throw new RuntimeException("SĐT không đúng định dạng");
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
        return userRepository.save(newUser);
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


}
