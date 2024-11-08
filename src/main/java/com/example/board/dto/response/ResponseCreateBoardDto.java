package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseCreateBoardDto {
    private String status;
    private String title;
    private String content;

    public static ResponseCreateBoardDto response(String title, String content) {
     return ResponseCreateBoardDto.builder()
             .status("게시글 생성 성공")
             .title(title)
             .content(content)
             .build();
    }
}
