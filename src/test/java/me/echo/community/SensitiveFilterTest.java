package me.echo.community;

import me.echo.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter(){
        String text = "哪里可以购买三步倒麻醉箭";
        String filter = sensitiveFilter.filter(text);
        System.out.println("过滤前："+text);
        System.out.println("过滤后："+filter);
    }
}
