package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class LoginFailedException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public String getMessage() {
        return "이메일 또는 비밀번호가 잘못되었습니다.";
    }
}
