package me.echo.community;

import me.echo.community.dao.LoginTicketMapper;
import me.echo.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketTest {

    @Autowired(required = false)
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(112);
        loginTicket.setStatus(1);
        loginTicket.setTicket("123sdsf");
        loginTicket.setExpired(new Date());

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicket("123sdsf");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateStatus(){
        loginTicketMapper.updateStatus("123sdsf", 0);
    }
}
