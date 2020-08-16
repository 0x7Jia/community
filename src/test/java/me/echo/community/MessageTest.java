package me.echo.community;

import me.echo.community.entity.Message;
import me.echo.community.service.MessageService;
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
public class MessageTest {
    @Autowired
    private MessageService messageService;

    @Test
    public void testFindConversation(){
        List<Message> conversations = messageService.findConversations(111, 0, 10);
        for (Message conversation : conversations) {
          System.out.println(conversation);
        }
    }

    @Test
    public void testSelectLetters(){
        List<Message> letters = messageService.findLetters("111_112", 0, 10);
        for (Message letter : letters) {
          System.out.println(letter);
        }
    }

    @Test
    public void testFindConversationCount(){
        int count = messageService.findConversationCount(111);
        System.out.println(count);
    }

    @Test
    public void testFindLetterCount(){
        int letterCount = messageService.findLetterCount("111_112");
        System.out.println(letterCount);
    }

    @Test
    public void testFindLetterUnreadCount(){
        int unreadCount = messageService.findLetterUnreadCount(111, null);
        System.out.println(unreadCount);
    }
}
