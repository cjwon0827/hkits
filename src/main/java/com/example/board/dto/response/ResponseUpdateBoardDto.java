package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseUpdateBoardDto {
    private String status;
    private String title;
    private String content;


    public static ResponseUpdateBoardDto response(String title, String content) {
     return ResponseUpdateBoardDto.builder()
             .status("게시글 수정 성공")
             .title(title)
             .content(content)
             .build();
    }
}
