package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
