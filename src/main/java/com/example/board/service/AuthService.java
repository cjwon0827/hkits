package com.example.board.service;

import com.example.board.dto.request.RequestLoginDto;
import com.example.board.dto.request.RequestRegisterDto;
import com.example.board.dto.response.ResponseLoginDto;
import com.example.board.dto.response.ResponseRegisterDto;

public interface AuthService {
    ResponseRegisterDto register(RequestRegisterDto request);
    ResponseLoginDto login(RequestLoginDto request);
}
