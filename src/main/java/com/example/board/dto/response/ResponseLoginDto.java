package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ResponseLoginDto {
    private String accessToken;
    private String refreshToken;
    private String message;
    private String role;
}
