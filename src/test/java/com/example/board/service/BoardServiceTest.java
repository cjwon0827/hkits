package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;
import com.example.board.exception.NotExistBoardException;
import com.example.board.mapper.BoardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BoardServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(BoardServiceTest.class);
    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board1;
    private Board board2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {


        board1 = Board.builder()
                .id(1L)
                .title("테스트 제목1")
                .content("테스트 내용1")
                .createdAt(now)
                .build();

        board2 = Board.builder()
                .id(2L)
                .title("테스트 제목2")
                .content("테스트 내용2")
                .createdAt(now)
                .build();
    }

    @Test
    @DisplayName("모든 게시글을 가져오는 테스트")
    void getAllBoards() {
        // given
        List<Board> boards = Arrays.asList(board1, board2);
        when(boardMapper.findAll()).thenReturn(boards);

        // when
        List<ResponseBoardDto> result = boardService.getAllBoards();

        // then
        assertEquals(2, result.size());
        assertThat(result.get(0))
                .extracting("id", "title", "content", "createdAt")
                .containsExactly(1L, "테스트 제목1", "테스트 내용1", now);

        assertThat(result.get(1))
                .extracting("id", "title", "content", "createdAt")
                .containsExactly(2L, "테스트 제목2", "테스트 내용2", now);

        logger.info("모든 게시글을 가져오는 테스트가 성공적으로 통과되었습니다.");
    }

    @Test
    @DisplayName("ID로 게시글을 가져오는 테스트")
    void getBoardById() {
        // given
        when(boardMapper.findById(1L)).thenReturn(board1);

        // when
        ResponseBoardDto result = boardService.getBoardById(1L);

        // then
        assertThat(result)
                .extracting("id", "title", "content", "createdAt")
                .containsExactly(1L, "테스트 제목1", "테스트 내용1", now);

        logger.info("ID로 게시글을 가져오는 테스트가 성공적으로 통과되었습니다.");
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    void createBoard() {
        // given
        RequestBoardDto requestBoardDto = new RequestBoardDto("new title", "new content");

        // when
        boardService.createBoard(requestBoardDto);

        // then
        verify(boardMapper, times(1)).save(any(Board.class));
        logger.info("게시글 생성 테스트가 성공적으로 통과되었습니다.");
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updateBoard() {
        // given
        Long boardId = 1L;
        RequestBoardDto requestBoardDto = new RequestBoardDto("updated title", "updated content");

        // when
        boardService.updateBoard(boardId, requestBoardDto);

        // then
        verify(boardMapper, times(1)).update(any(Board.class));
        logger.info("게시글 수정 테스트가 성공적으로 통과되었습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 성공")
    void deleteBoard_Success() {
        // given
        Board board = new Board(1L, "title", "content", 0, LocalDateTime.now(), null);
        given(boardMapper.findById(1L)).willReturn(board);

        // when
        boardService.deleteBoard(1L);

        // then
        verify(boardMapper, times(1)).deleteById(1L);
        logger.info("게시글 삭제 테스트가 성공적으로 통과되었습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 실패 (게시글 없음)")
    void deleteBoard_NotExist() {
        // given
        given(boardMapper.findById(1L)).willReturn(null);

        // when / then
        assertThrows(NotExistBoardException.class, () -> boardService.deleteBoard(1L));
        logger.info("게시글 삭제 테스트 - 실패가 성공적으로 통과되었습니다.");
    }
}
