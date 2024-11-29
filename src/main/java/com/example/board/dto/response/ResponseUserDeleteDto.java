package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseUserDeleteDto {
    private String message;

    public static ResponseUserDeleteDto responseUserDelete(){
        return ResponseUserDeleteDto.builder()
                .message("회원 탈퇴가 완료되었습니다.")
                .build();
    }
}
