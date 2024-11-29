package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseLogoutDto {
    private String message;

    public static ResponseLogoutDto responseLogout(){
        return ResponseLogoutDto.builder()
                .message("로그아웃 되었습니다.")
                .build();
    }
}
