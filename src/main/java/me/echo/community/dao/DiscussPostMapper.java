package me.echo.community.dao;

import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.DiscussPostWithUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {
    /**
     * 按用户 id 查询 post
     * @param userId userId 为 0 则查询所有，否则按给定的 userId 查询该用户的 post
     */
    List<DiscussPost> selectDiscussPosts(Integer userId, int offset, int limit);

    /**
     * 查询用户的 post 总数
     * @param userId userId 为 0 则查询所有
     */
    int selectDiscussPostRows(@Param("userId") int userId);


    List<DiscussPostWithUser> selectDiscussPostWithUser(Integer userId, int offset, int limit);

    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据id查询帖子详情
     * @param id 帖子id
     */
    DiscussPost selectDiscussPostById(Integer id);

    int updateCommentCount(Integer id, Integer commentCount);
}
