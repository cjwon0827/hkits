package com.example.board.type;

import lombok.Builder;

@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
