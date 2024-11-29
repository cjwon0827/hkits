package com.example.board.service;

import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;
import com.example.board.dto.response.ResponseBoardListDto;

import java.util.List;

public interface BoardService {
    List<ResponseBoardListDto> getAllBoards();
    ResponseBoardDto getBoardById(Long id);
    void createBoard(RequestBoardDto board, Long userId);
    void updateBoard(Long id, RequestBoardDto board, Long userId);
    void deleteBoard(Long boardId, Long userId);

}
