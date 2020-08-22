package me.echo.community;

import me.echo.community.dao.DiscussPostMapper;
import me.echo.community.dao.UserMapper;
import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.DiscussPostWithUser;
import me.echo.community.entity.User;
import me.echo.community.enums.UserType;
import me.echo.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testSelectDiscussPostRows(){
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testSelectDiscussPosts(){
//        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(101, 2, 4);
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPosts(101, 2, 4, 0);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Test
    public void testSelectUserById() {
        User users = userMapper.selectUserById(115);
        System.out.println(users);
    }

    @Test
    public void testSelectDiscussPostWithUser() {
        List<DiscussPostWithUser> discussPostWithUsers = discussPostMapper.selectDiscussPostWithUser(101, 1, 4, 0);
        for (DiscussPostWithUser discussPostWithUser : discussPostWithUsers) {
            System.out.println(discussPostWithUser);
        }
    }

    @Test
    public void testEnum() {
        System.out.println(UserType.SUPERUSER.getKey());
    }
}
