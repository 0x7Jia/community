package me.echo.community.controller;

import me.echo.community.entity.Comment;
import me.echo.community.entity.DiscussPost;
import me.echo.community.entity.Page;
import me.echo.community.entity.User;
import me.echo.community.service.CommentService;
import me.echo.community.service.DiscussPostService;
import me.echo.community.service.LikeService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

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
    public String getPostDetail(@PathVariable("postId") Integer postId, Model model, Page page){
        if (postId < 0){
            return "/index";
        }

        DiscussPost post = discussPostService.findDiscussPostById(postId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 帖子点赞信息
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        model.addAttribute("likeCount", likeCount);
        // 当前用户是否对帖子点过赞
        int likeStatus = hostHolder.getUser() == null? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, postId);
        model.addAttribute("likeStatus",likeStatus);

        // 查询评论
        page.setLimit(10);
        page.setPath("/discuss/detail/"+postId);
        page.setRows(post.getCommentCount());

        // 给帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList!=null){
            for (Comment comment:commentList){
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 评论点赞信息
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 当前用户是否对评论点过赞
                likeStatus = hostHolder.getUser() == null? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus",likeStatus);

                // 回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList!=null){
                    for (Comment reply:replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 回复点赞信息
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // 当前用户是否对回复点过赞
                        likeStatus = hostHolder.getUser() == null? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
