package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseRegisterDto {
    private String message;
    private String email;
    private LocalDateTime registeredAt;

    public static ResponseRegisterDto responseRegister(String email, LocalDateTime registeredAt) {
        return ResponseRegisterDto.builder()
                .message("회원가입이 완료되었습니다.")
                .email(email)
                .registeredAt(registeredAt)
                .build();
    }
}
