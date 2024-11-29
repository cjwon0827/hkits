package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.dto.request.RequestBoardDto;
import com.example.board.dto.response.ResponseBoardListDto;
import com.example.board.dto.response.ResponseCreateBoardDto;
import com.example.board.dto.response.ResponseDeleteBoardDto;
import com.example.board.dto.response.ResponseUpdateBoardDto;
import com.example.board.exception.NotExistBoardException;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    /**
     * 게시판의 모든 리스트를 가져오는 API
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllBoards() {
        List<ResponseBoardListDto> boards = boardService.getAllBoards();

        if(boards.isEmpty()) {
            return ResponseEntity.ok("게시글이 존재하지 않습니다.");
        }

        return ResponseEntity.ok(boards);
    }

    /**
     * 게시글을 상세 조회하는 API
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    /**
     * 게시글을 생성하는 API
     * @param board
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody RequestBoardDto board, @AuthenticationPrincipal User user) {
        boardService.createBoard(board, user.getId());
        return ResponseEntity.ok(ResponseCreateBoardDto.response(board.getTitle(), board.getContent()));
    }

    /**
     * 게시글을 수정하는 API
     * @param boardId
     * @param board
     * @return
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long boardId, @RequestBody RequestBoardDto board, @AuthenticationPrincipal User user) {
        boardService.updateBoard(boardId, board, user.getId());
        return ResponseEntity.ok((ResponseUpdateBoardDto.response(board.getTitle(), board.getContent())));
    }

    /**
     * 게시글을 삭제하는 API
     * @param boardId
     * @param user
     * @return
     * @throws NotExistBoardException
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Long boardId, @AuthenticationPrincipal User user) {
        boardService.deleteBoard(boardId, user.getId());
        return ResponseEntity.ok((ResponseDeleteBoardDto.response(boardId)));
    }
}