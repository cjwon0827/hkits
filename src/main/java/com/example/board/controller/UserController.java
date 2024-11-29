package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.dto.request.RequestLoginDto;
import com.example.board.dto.request.RequestRegisterDto;
import com.example.board.dto.request.RequestUserDeleteDto;
import com.example.board.dto.response.ResponseLoginDto;
import com.example.board.dto.response.ResponseLogoutDto;
import com.example.board.dto.response.ResponseRegisterDto;
import com.example.board.dto.response.ResponseUserDeleteDto;
import com.example.board.mapper.UserMapper;
import com.example.board.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserMapper userMapper;

    /**
     * 회원가입을 진행하는 API
     *
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestRegisterDto request) {
        ResponseRegisterDto response = authService.register(request);
        return ResponseEntity.ok(ResponseRegisterDto.responseRegister(response.getEmail(), response.getRegisteredAt()));
    }

    /**
     * 로그인을 실행하는 API
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDto request) {
        ResponseLoginDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃을 실행하는 API
     *
     * @param accessToken
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ResponseEntity.ok(ResponseLogoutDto.responseLogout());
    }


    /**
     * 회원탈퇴를 실행하는 API
     *
     * @param request
     * @return
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> userDelete(@PathVariable("id") Long id, @RequestBody RequestUserDeleteDto request, @AuthenticationPrincipal User user) {
        User currentUser = userMapper.findById(user.getId());
        authService.userDelete(id, request, currentUser);
        return ResponseEntity.ok(ResponseUserDeleteDto.responseUserDelete());

    }
}
