package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    public String getMessage() {
        return "본인만 탈퇴할 수 있습니다.";
    }
}
