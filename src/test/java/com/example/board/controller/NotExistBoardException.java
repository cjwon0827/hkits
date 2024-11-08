package com.example.board.controller;

import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;
import com.example.board.dto.response.ResponseCreateBoardDto;
import com.example.board.dto.response.ResponseDeleteBoardDto;
import com.example.board.dto.response.ResponseUpdateBoardDto;
import com.example.board.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 목록 조회 - 게시글이 있는 경우")
    void getAllBoards_WithExistingBoards() throws Exception {
        // given
        List<ResponseBoardDto> boards = new ArrayList<>();
        boards.add(new ResponseBoardDto(1L, "제목1", "내용1",  LocalDateTime.now(), null, 0));
        boards.add(new ResponseBoardDto(2L, "제목2", "내용2",  LocalDateTime.now(), null, 0));
        given(boardService.getAllBoards()).willReturn(boards);

        // when & then
        mockMvc.perform(get("/board"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boards)));
    }

    @Test
    @DisplayName("게시글 목록 조회 - 게시글이 없는 경우")
    void getAllBoards_WithNoBoards() throws Exception {
        // given
        given(boardService.getAllBoards()).willReturn(new ArrayList<>());

        // when & then
        mockMvc.perform(get("/board"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("게시글 상세 조회")
    void getBoardById() throws Exception {
        // given
        Long boardId = 1L;
        ResponseBoardDto board = new ResponseBoardDto(1L, "제목1", "내용1",  LocalDateTime.now(), null, 0);
        given(boardService.getBoardById(boardId)).willReturn(board);

        // when & then
        mockMvc.perform(get("/board/{id}", boardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(board)));
    }

    @Test
    @DisplayName("게시글 생성")
    void createBoard() throws Exception {
        // given
        RequestBoardDto request = new RequestBoardDto("새 제목", "새 내용");
        ResponseCreateBoardDto response = ResponseCreateBoardDto.response("새 제목", "새 내용");
        doNothing().when(boardService).createBoard(any(RequestBoardDto.class));

        // when & then
        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("게시글 수정")
    void updateBoard() throws Exception {
        // given
        Long boardId = 1L;
        RequestBoardDto request = new RequestBoardDto("수정된 제목", "수정된 내용");
        ResponseUpdateBoardDto response = ResponseUpdateBoardDto.response("수정된 제목", "수정된 내용");
        doNothing().when(boardService).updateBoard(eq(boardId), any(RequestBoardDto.class));

        // when & then
        mockMvc.perform(post("/board/update/{id}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteBoard() throws Exception {
        // given
        Long boardId = 1L;
        ResponseDeleteBoardDto response = ResponseDeleteBoardDto.response(boardId);
        doNothing().when(boardService).deleteBoard(boardId);

        // when & then
        mockMvc.perform(post("/board/delete/{id}", boardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시도")
    void deleteBoard_NotExist() throws Exception {
        // given
        Long boardId = 999L;
        willThrow(new NotExistBoardException("게시글이 존재하지 않습니다."))
                .given(boardService).deleteBoard(boardId);

        // when & then
        mockMvc.perform(post("/board/delete/{id}", boardId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않습니다."));
    }
}

// ExceptionHandler 추가
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotExistBoardException.class)
    public ResponseEntity<Map<String, String>> handleNotExistBoardException(NotExistBoardException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

// Exception 클래스
public class NotExistBoardException extends RuntimeException {
    public NotExistBoardException(String message) {
        super(message);
    }
}
