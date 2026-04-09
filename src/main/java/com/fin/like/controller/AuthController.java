package com.fin.like.controller;

import com.fin.like.common.response.ApiResponse;
import com.fin.like.dto.LoginRequest;
import com.fin.like.dto.RegisterRequest;
import com.fin.like.dto.UserDto;
import com.fin.like.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login — 帳號密碼登入 */
    @PostMapping("/login")
    public ApiResponse<UserDto> login(@RequestBody LoginRequest request) {
        UserDto user = authService.login(request);
        return ApiResponse.success(user);
    }

    /** POST /api/auth/register — 申請帳號 */
    @PostMapping("/register")
    public ApiResponse<UserDto> register(@RequestBody RegisterRequest request) {
        UserDto user = authService.register(request);
        return ApiResponse.success(user);
    }
}
