package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class LogoutFailedException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getMessage() {
        return "로그아웃 처리 중 오류가 발생했습니다.";
    }
}
