//package com.example.board.controller;
//
//import com.example.board.config.SecurityConfig;
//import com.example.board.domain.User;
//import com.example.board.dto.request.RequestBoardDto;
//import com.example.board.dto.response.ResponseBoardDto;
//import com.example.board.dto.response.ResponseBoardListDto;
//import com.example.board.exception.NotExistBoardException;
//import com.example.board.service.BoardService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(BoardController.class)
//@Import(SecurityConfig.class)  // 보안 설정 Import
//class BoardControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private BoardService boardService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private RequestBoardDto testBoardDto;
//    private ResponseBoardDto testResponseBoardDto;
//    private ResponseBoardListDto testBoardResponse;
//
//    @BeforeEach
//    void setUp() {
//        testBoardDto = new RequestBoardDto("Test Title", "Test Content");
//
//        testResponseBoardDto = ResponseBoardDto.builder()
//                .id(1L)
//                .title("Test Title")
//                .content("Test Content")
//                .build();
//        testBoardResponse = ResponseBoardListDto.builder()
//                .id(1L)
//                .title("Test Title")
//                .build();
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 목록 조회 - 성공")
//    void getAllBoards_Success() throws Exception {
//        // given
//        given(boardService.getAllBoards())
//                .willReturn(Arrays.asList(testBoardResponse));
//
//        // when & then
//        mockMvc.perform(get("/board")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(testBoardResponse))));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 목록 조회 - 빈 목록")
//    void getAllBoards_EmptyList() throws Exception {
//        // given
//        given(boardService.getAllBoards())
//                .willReturn(Collections.emptyList());
//
//        // when & then
//        mockMvc.perform(get("/board")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string("게시글이 존재하지 않습니다."));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 상세 조회 - 성공")
//    void getBoardById_Success() throws Exception {
//        // given
//        given(boardService.getBoardById(1L))
//                .willReturn(testResponseBoardDto);
//
//        // when & then
//        mockMvc.perform(get("/board/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(testBoardResponse)));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 생성 - 성공")
//    void createBoard_Success() throws Exception {
//        // given
//        RequestBoardDto newBoard = new RequestBoardDto("New Title", "New Content");
//        doNothing().when(boardService).createBoard(any(RequestBoardDto.class), any(Long.class));
//
//        // when & then
//        mockMvc.perform(post("/board")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newBoard)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value(newBoard.getTitle()))
//                .andExpect(jsonPath("$.content").value(newBoard.getContent()));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 수정 - 성공")
//    void updateBoard_Success() throws Exception {
//        // given
//        RequestBoardDto updateBoard = new RequestBoardDto("Updated Title", "Updated Content");
//        doNothing().when(boardService).updateBoard(eq(1L), any(RequestBoardDto.class), any(Long.class));
//
//        // when & then
//        mockMvc.perform(post("/board/update/1")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateBoard)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value(updateBoard.getTitle()))
//                .andExpect(jsonPath("$.content").value(updateBoard.getContent()));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 삭제 - 성공")
//    void deleteBoard_Success() throws Exception {
//        // given
//        doNothing().when(boardService).deleteBoard(eq(1L), any(Long.class));
//
//        // when & then
//        mockMvc.perform(post("/board/delete/1")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.boardId").value(1));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com", roles = "USER")
//    @DisplayName("게시글 상세 조회 - 실패(존재하지 않는 게시글)")
//    void getBoardById_NotFound() throws Exception {
//        // given
//        given(boardService.getBoardById(999L))
//                .willThrow(new NotExistBoardException());
//
//        // when & then
//        mockMvc.perform(get("/board/999")
//                        .with(csrf()))
//                .andExpect(status().isNotFound());
//    }
//}