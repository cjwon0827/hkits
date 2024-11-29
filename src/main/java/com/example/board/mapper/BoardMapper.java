package com.example.board.mapper;

import com.example.board.domain.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> findAll();
    Board findById(Long id);
    void save(Board board);
    void update(Board board);
    void deleteById(Long id);
    void increaseViewCount(Long id);
    Long findFirstEmptyId();
    void resetSequence();
    int getCount();
    Long getMaxId();
    void deleteByUserId(Long userId);
}
