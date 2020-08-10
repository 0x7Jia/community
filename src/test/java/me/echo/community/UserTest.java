package me.echo.community;

import me.echo.community.dao.UserMapper;
import me.echo.community.entity.User;
import me.echo.community.enums.UserStatus;
import me.echo.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UserTest {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUserName("echo");
        user.setEmail("echo.me@hotmail.com");
        user.setPassword("1234567");
        System.out.println("before " + user.getId());

        Map<String, Object> register = userService.register(user);
        System.out.println(register);

        System.out.println("after " + user.getId());
    }

    @Test
    public void testUpdateStatus(){
        userMapper.updateStatus(158, UserStatus.ACTIVATED.getKey());
    }
}
