package me.echo.community.controller;

import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.DiscussPostWithUser;
import me.echo.community.entity.Page;
import me.echo.community.service.DiscussPostService;
import me.echo.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Map<String, Object> map, Page page){
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPostWithUser> discussPosts = discussPostService.selectDiscussPostWithUser(0, page.getOffset(), page.getLimit());

        map.put("posts", discussPosts);

        return "index";
    }
}
