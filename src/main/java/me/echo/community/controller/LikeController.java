package me.echo.community.controller;

import me.echo.community.entity.User;
import me.echo.community.service.LikeService;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    private final LikeService likeService;

    private final HostHolder hostHolder;

    public LikeController(LikeService likeService, HostHolder hostHolder) {
        this.likeService = likeService;
        this.hostHolder = hostHolder;
    }


    /**
     * 点赞
     * @param entityType
     * @param entityId
     * @return JSON格式字符串，封装点赞总数和该用户的点赞状态
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId){
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, "success", map);
    }
}
