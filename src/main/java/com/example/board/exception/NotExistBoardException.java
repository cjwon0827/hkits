package com.example.board.exception;

import org.springframework.http.HttpStatus;

public class NotExistBoardException extends AbstractException{
    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getMessage() {
        return "해당 게시글이 존재하지 않습니다.";
    }
}
