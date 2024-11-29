package com.example.board.mapper;

import com.example.board.domain.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {
    User findByEmail(String email);
    User findById(Long id);
    int existByEmail(String email);
    void save(User user);
    void deleteById(Long userId);
}
