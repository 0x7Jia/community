package me.echo.community.dao;

import me.echo.community.entity.User;

import java.util.List;

public interface UserMapper {
    List<User> selectUserById(Integer id);
}
