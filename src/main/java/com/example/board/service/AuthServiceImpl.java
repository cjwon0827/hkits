package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.dto.request.RequestLoginDto;
import com.example.board.dto.request.RequestRegisterDto;
import com.example.board.dto.response.ResponseLoginDto;
import com.example.board.dto.response.ResponseRegisterDto;
import com.example.board.mapper.UserMapper;
import com.example.board.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입을 진행할 수 있는 서비스
     * @param request
     * @return
     */
    @Override
    public ResponseRegisterDto register(RequestRegisterDto request) {
        if (userMapper.existByEmail(request.getEmail()) > 0){
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER.toString())
                .createdAt(LocalDateTime.now())
                .build();

        userMapper.save(user);

        return ResponseRegisterDto.builder()
                .message("회원가입이 완료되었습니다.")
                .email(user.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();
    }

    /**
     * 로그인을 실행하는 서비스
     * @param request
     * @return
     */
    @Override
    public ResponseLoginDto login(RequestLoginDto request) {
        // 새로운 로그인 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        return ResponseLoginDto.builder()
                .message("로그인이 완료되었습니다.")
                .role("권한 : " + user.getRole())
                .build();
    }
}
