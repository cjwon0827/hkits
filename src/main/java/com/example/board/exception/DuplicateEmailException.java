package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends AbstractException{
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 이메일입니다.";
    }
}
