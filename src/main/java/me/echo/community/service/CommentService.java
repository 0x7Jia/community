package me.echo.community.service;

import me.echo.community.dao.CommentMapper;
import me.echo.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    public List<Comment> findCommentByEntity(Integer entityType, Integer entityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(Integer entityType, Integer entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
}
