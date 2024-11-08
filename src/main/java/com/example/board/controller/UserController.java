package com.example.board.controller;

import com.example.board.dto.request.RequestLoginDto;
import com.example.board.dto.request.RequestRegisterDto;
import com.example.board.dto.response.ResponseLoginDto;
import com.example.board.dto.response.ResponseRegisterDto;
import com.example.board.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    /**
     * 회원가입을 진행하는 API
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterDto> register(@RequestBody RequestRegisterDto request) {
        try {
            ResponseRegisterDto response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ResponseRegisterDto.builder()
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * 로그인을 실행하는 API
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody RequestLoginDto request, HttpSession session) {
        try {
            ResponseLoginDto response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseLoginDto.builder()
                            .message("이메일 또는 비밀번호가 잘못되었습니다.")
                            .build());
        }
    }

    /**
     * 로그아웃을 실행하는 API
     * @param request
     * @return
     */
    @PostMapping(value = "/logout")
    public ResponseEntity<ResponseLoginDto> logout(HttpServletRequest request) {
        try {
            request.logout();

            return ResponseEntity.ok(ResponseLoginDto.builder()
                    .message("로그아웃 되었습니다.")
                    .build());
        } catch (ServletException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseLoginDto.builder()
                            .message("로그아웃 처리 중 오류가 발생했습니다.")
                            .build());
        }
    }
}
