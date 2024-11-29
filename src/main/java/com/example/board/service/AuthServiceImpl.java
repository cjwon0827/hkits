package com.example.board.service;

import com.example.board.component.JwtTokenProvider;
import com.example.board.domain.User;
import com.example.board.dto.request.RequestLoginDto;
import com.example.board.dto.request.RequestRegisterDto;
import com.example.board.dto.request.RequestUserDeleteDto;
import com.example.board.dto.response.ResponseLoginDto;
import com.example.board.dto.response.ResponseRegisterDto;
import com.example.board.exception.*;
import com.example.board.mapper.BoardMapper;
import com.example.board.mapper.UserMapper;
import com.example.board.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final BoardMapper boardMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원가입을 진행할 수 있는 서비스
     * @param request
     * @return
     */
    @Override
    public ResponseRegisterDto register(RequestRegisterDto request) {
        if (userMapper.existByEmail(request.getEmail()) > 0){
            throw new DuplicateEmailException();
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
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            throw new LoginFailedException();
        }

    }


    /**
     * 로그아웃을 실행하는 서비스
     * @param accessToken
     */
    @Override
    @Transactional
    public void logout(String accessToken) {
        try {
            // Bearer 제거
            String token = accessToken.substring(7);

            // 현재 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            // 토큰 남은 유효시간 계산
            Date expiration = jwtTokenProvider.getExpirationFromToken(token);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();

            // Redis에 블랙리스트로 등록
            redisTemplate.opsForValue()
                    .set("BL:" + token, "logout", remainingTime, TimeUnit.MILLISECONDS);

            // Redis에서 Refresh Token 삭제
            redisTemplate.delete("RT:" + user.getEmail());

            // SecurityContext 클리어
            SecurityContextHolder.clearContext();
        } catch (Exception e){
            throw new LogoutFailedException();
        }
    }


    /**
     * 회원탈퇴를 진행하는 서비스
     * @param id
     * @param request
     * @param currentUser
     */
    @Override
    @Transactional
    public void userDelete(Long id, RequestUserDeleteDto request, User currentUser) {
        if (!currentUser.getId().equals(id)) {
            throw new UnauthorizedAccessException();
        }

        if (!passwordEncoder.matches(request.getPassword(), currentUser.getPassword())){
            throw new PasswordNotSameException();
        }

        boardMapper.deleteByUserId(currentUser.getId());
        userMapper.deleteById(id);
    }
}