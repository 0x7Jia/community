package me.echo.community.service;

import me.echo.community.dao.DiscussPostMapper;
import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.DiscussPostWithUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> selectDiscussPosts(Integer userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public List<DiscussPostWithUser> selectDiscussPostWithUser(Integer userId, int offset, int limit){
        return discussPostMapper.selectDiscussPostWithUser(userId, offset, limit);
    }

    public int selectDiscussPostRows(Integer userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
