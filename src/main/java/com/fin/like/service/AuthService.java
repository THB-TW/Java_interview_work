package com.fin.like.service;

import com.fin.like.common.exception.BusinessException;
import com.fin.like.dto.LoginRequest;
import com.fin.like.dto.RegisterRequest;
import com.fin.like.dto.UserDto;
import com.fin.like.repository.UserCredentialsRepository;
import com.fin.like.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserCredentialsRepository credentialsRepository,
                       UserRepository userRepository) {
        this.credentialsRepository = credentialsRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /** 登入：驗證 username + password，成功回傳 UserDto */
    public UserDto login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw BusinessException.badRequest("帳號不可為空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw BusinessException.badRequest("密碼不可為空");
        }

        // 查 user_credentials
        Optional<String[]> credOpt = credentialsRepository.findByUsername(request.getUsername());
        if (credOpt.isEmpty()) {
            throw BusinessException.badRequest("帳號或密碼錯誤");
        }

        String[] cred = credOpt.get();
        String userId = cred[0];
        String hashedPassword = cred[1];

        // BCrypt 驗證
        if (!passwordEncoder.matches(request.getPassword(), hashedPassword)) {
            throw BusinessException.badRequest("帳號或密碼錯誤");
        }

        // 回傳用戶資料
        return userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("找不到對應的使用者資料"));
    }

    /** 申請帳號：同時寫入 users + user_credentials */
    @Transactional
    public UserDto register(RegisterRequest request) {
        // 基本驗證
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw BusinessException.badRequest("userId 不可為空");
        }
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            throw BusinessException.badRequest("姓名不可為空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw BusinessException.badRequest("email 不可為空");
        }
        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw BusinessException.badRequest("銀行帳號不可為空");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw BusinessException.badRequest("登入帳號不可為空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw BusinessException.badRequest("密碼長度至少 6 碼");
        }

        // 唯一性確認
        if (userRepository.existsById(request.getUserId())) {
            throw BusinessException.badRequest("此 userId 已存在");
        }
        if (credentialsRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.badRequest("此登入帳號已被使用");
        }

        // 寫入 users
        UserDto newUser = new UserDto();
        newUser.setUserId(request.getUserId());
        newUser.setUserName(request.getUserName());
        newUser.setEmail(request.getEmail());
        newUser.setAccount(request.getAccount());
        userRepository.insert(newUser);

        // 寫入 user_credentials（密碼雜湊）
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        credentialsRepository.insert(request.getUserId(), request.getUsername(), hashedPassword);

        return newUser;
    }
}
