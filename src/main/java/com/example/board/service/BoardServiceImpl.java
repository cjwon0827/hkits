package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardDto;
import com.example.board.exception.NotExistBoardException;
import com.example.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<ResponseBoardDto> getAllBoards() {
        return boardMapper.findAll().stream()
                .map(board -> ResponseBoardDto.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .createdAt(board.getCreatedAt())
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
        Board board = boardMapper.findById(id);
        return ResponseBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .build();
    }

    @Override
    public void createBoard(RequestBoardDto requestBoardDto) {
        Board board = Board.builder()
                .title(requestBoardDto.getTitle())
                .content(requestBoardDto.getContent())
                .createdAt(LocalDateTime.now())
                .viewCount(0)
                .build();

        boardMapper.save(board);
    }


    /**
     * 게시글을 수정할 수 있는 서비스
     * @param id
     * @param requestBoardDto
     */
    @Override
    public void updateBoard(Long id, RequestBoardDto requestBoardDto) {
        Board board = Board.builder()
                .id(id)
                .title(requestBoardDto.getTitle())
                .content(requestBoardDto.getContent())
                .updatedAt(LocalDateTime.now())
                .build();

        boardMapper.update(board);
    }

    /**
     * 게시글을 삭제하는 서비스
     * (단, 존재하지 않는 게시글을 삭제하려고 할 시 예외 발섕NotExistBoardException())
     * @param id
     */
    @Override
    public void deleteBoard(Long id) {
        Board board = Optional.ofNullable(boardMapper.findById(id))
                        .orElseThrow(() -> new NotExistBoardException());

        boardMapper.deleteById(id);
    }
}
