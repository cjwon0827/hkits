package com.example.board.service;

import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;

import java.util.List;

public interface BoardService {
    List<ResponseBoardDto> getAllBoards();
    ResponseBoardDto getBoardById(Long id);
    void createBoard(RequestBoardDto board);
    void updateBoard(Long id, RequestBoardDto board);
    void deleteBoard(Long id);

}
