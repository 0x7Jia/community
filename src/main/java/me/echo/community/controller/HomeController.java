package me.echo.community.controller;

import me.echo.community.entity.DiscussPostWithUser;
import me.echo.community.entity.Page;
import me.echo.community.service.DiscussPostService;
import me.echo.community.service.LikeService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping(path = {"/", "/index"})
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPostWithUser> list = discussPostService.selectDiscussPostWithUser(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (list!=null){
            for (DiscussPostWithUser discussPostWithUser:list){
                Map<String, Object> map = new HashMap<>();
                map.put("postWithUser", discussPostWithUser);

                // 帖子的总赞数查询
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostWithUser.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);

        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }


    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }
}
