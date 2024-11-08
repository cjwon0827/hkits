package com.example.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseDeleteBoardDto {
    private String status;
    private Long boardId;

    public static ResponseDeleteBoardDto response(Long boardId) {
        return ResponseDeleteBoardDto.builder()
                .status("게시글 삭제 성공")
                .boardId(boardId)
                .build();
    }


}
