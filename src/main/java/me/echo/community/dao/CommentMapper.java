package me.echo.community.dao;

import me.echo.community.entity.Comment;

import java.util.List;

public interface CommentMapper {
    List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, int offset, int limit);

    int selectCountByEntity(Integer entityType, Integer entityId);

    int insertComment(Comment comment);
}
