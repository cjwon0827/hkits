package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;
import com.example.board.dto.response.ResponseBoardListDto;
import com.example.board.exception.NotExistBoardException;
import com.example.board.exception.UnauthorizedBoardAccessException;
import com.example.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;

    /**
     * 게시판의 모든 게시물을 조회하는 서비스
     * @return
     */
    @Override
    public List<ResponseBoardListDto> getAllBoards() {
        return boardMapper.findAll().stream()
                .map(board -> ResponseBoardListDto.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .createdAt(board.getCreatedAt())
                        .viewCount(board.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 게시글을 상세 조회할 수 있는 서비스
     * @param id
     * @return
     */
    @Override
    public ResponseBoardDto getBoardById(Long id) {
        Board board = Optional.ofNullable(boardMapper.findById(id))
                .orElseThrow(() -> new NotExistBoardException());

        //조회수 증가
        boardMapper.increaseViewCount(id);

        return ResponseBoardDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .viewCount(board.getViewCount())
                .userId(board.getUserId())
                .build();
    }

    @Override
    @Transactional
    public void createBoard(RequestBoardDto requestBoardDto, Long userId) {
        Long nextId = generateNextBoardId();

        Board board = Board.builder()
                .id(nextId)
                .title(requestBoardDto.getTitle())
                .content(requestBoardDto.getContent())
                .createdAt(LocalDateTime.now())
                .viewCount(0)
                .userId(userId)
                .build();

        boardMapper.save(board);
    }


    /**
     * 다음 사용할 게시판 ID를 생성하는 메서드
     * @return 다음 사용할 ID
     * @throws RuntimeException ID 생성 중 충돌이 발생한 경우
     */
    private Long generateNextBoardId() {
        // 테이블이 비어있는 경우 시퀀스 초기화 및 1 반환
        if (boardMapper.getCount() == 0) {
            boardMapper.resetSequence();
            return 1L;
        }

        // 비어있는 ID 찾기
        Long nextId = boardMapper.findFirstEmptyId();

        // 비어있는 ID가 없는 경우 최대값 + 1 사용
        if (nextId == null) {
            nextId = boardMapper.getMaxId() + 1;
        }

        // ID 충돌 체크
        if (boardMapper.findById(nextId) != null) {
            throw new RuntimeException("ID 생성 중 충돌이 발생했습니다. 다시 시도해주세요.");
        }

        return nextId;
    }


    /**
     * 게시글을 수정할 수 있는 서비스
     * @param boardId
     * @param requestBoardDto
     * @param userId
     */
    @Override
    @Transactional
    public void updateBoard(Long boardId, RequestBoardDto requestBoardDto, Long userId) {
        ResponseBoardDto existingBoard = getBoardById(boardId);

        if (!existingBoard.getUserId().equals(userId)) {
            throw new UnauthorizedBoardAccessException();
        }

        Board board = Board.builder()
                .id(boardId)
                .title(requestBoardDto.getTitle())
                .content(requestBoardDto.getContent())
                .updatedAt(LocalDateTime.now())
                .build();

        boardMapper.update(board);
    }


    @Override
    public void deleteBoard(Long boardId, Long userId) {
        ResponseBoardDto existingBoard = getBoardById(boardId);
        if (!existingBoard.getUserId().equals(userId)) {
            throw new UnauthorizedBoardAccessException();
        }

        boardMapper.deleteById(boardId);
    }
}
