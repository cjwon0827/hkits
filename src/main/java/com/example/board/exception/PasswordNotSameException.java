package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class PasswordNotSameException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public String getMessage() {
        return "비밀번호가 일치하지 않습니다.";
    }
}
