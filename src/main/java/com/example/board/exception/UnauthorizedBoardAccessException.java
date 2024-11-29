package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedBoardAccessException extends AbstractException{
    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value(); // 403 상태코드 반환
    }

    @Override
    public String getMessage() {
        return "게시글의 작성자만 접근할 수 있습니다.";
    }
}
