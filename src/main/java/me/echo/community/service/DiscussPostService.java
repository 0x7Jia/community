package me.echo.community.service;

import me.echo.community.dao.DiscussPostMapper;
import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.DiscussPostWithUser;
import me.echo.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> selectDiscussPosts(Integer userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public List<DiscussPostWithUser> selectDiscussPostWithUser(Integer userId, int offset, int limit){
        return discussPostMapper.selectDiscussPostWithUser(userId, offset, limit);
    }

    public int selectDiscussPostRows(Integer userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 发布文章 过滤敏感词已经转义HTML
     * @param post DiscussPost对象
     */
    public int addDiscussPost(DiscussPost post){
        if (post==null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 转义HTML
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getContent()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(Integer id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(Integer id, Integer count){
        return discussPostMapper.updateCommentCount(id, count);
    }
}
