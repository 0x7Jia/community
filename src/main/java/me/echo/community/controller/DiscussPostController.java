package me.echo.community.controller;

import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.User;
import me.echo.community.service.DiscussPostService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403, "你还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(user.getId().toString());
        post.setType(0);
        post.setStatus(0);
        post.setCommentCount(0);
        post.setScore(0.);
        post.setCreateTime(new Date());

        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @GetMapping("/detail/{postId}")
    public String getPostDetail(@PathVariable("postId") Integer postId, Model model){
        if (postId < 0){
            return "/index";
        }

        DiscussPost post = discussPostService.findDiscussPostById(postId);
        model.addAttribute("post", post);
        User user = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
