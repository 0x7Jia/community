package me.echo.community.controller;

import me.echo.community.entity.Event;
import me.echo.community.entity.Page;
import me.echo.community.entity.User;
import me.echo.community.event.EventProducer;
import me.echo.community.service.FollowService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;


    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId)
                .setUserId(user.getId());
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "正在关注");
    }


    /**
     * 取关
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注");
    }


    /**
     * 获取关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") Integer userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在:"+userId);
        }
        model.addAttribute("user", user);

        page.setLimit(20);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList!=null){
            for (Map<String, Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }


    /**
     * 获取粉丝列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") Integer userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在:"+userId);
        }
        model.addAttribute("user", user);

        page.setLimit(20);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList!=null){
            for (Map<String, Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }

    /**
     * 获取当前登录的用户是否关注了目标用户
     * @param id 目标用户id
     * @return 是否关注，未登录则返回未关注
     */
    private boolean hasFollowed(Integer id) {
        User user = hostHolder.getUser();
        if (user==null){
            return false;
        }

        return followService.hasFollowed(user.getId(), ENTITY_TYPE_USER, id);
    }
}
