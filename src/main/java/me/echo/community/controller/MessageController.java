package me.echo.community.controller;

import com.alibaba.fastjson.JSONObject;
import me.echo.community.entity.Message;
import me.echo.community.entity.Page;
import me.echo.community.entity.User;
import me.echo.community.service.MessageService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController implements CommunityConstant {

    private final MessageService messageService;

    private final HostHolder hostHolder;

    private final UserService userService;

    public MessageController(MessageService messageService, HostHolder hostHolder, UserService userService) {
        this.messageService = messageService;
        this.hostHolder = hostHolder;
        this.userService = userService;
    }

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();

        // 设置分页信息
        page.setLimit(8);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList!=null){
            for (Message message:conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId().equals(message.getFromId()) ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnReadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        // 分页
        page.setLimit(8);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/"+conversationId);

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList!=null){
            for (Message letter:letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        if (letterList != null) {
            List<Integer> ids =
                letterList.stream()
                  .filter(
                      (t) -> (t.getStatus() == 0) && (t.getToId().equals(hostHolder.getUser().getId())))
                  .map(Message::getId)
                  .collect(Collectors.toList());
            // 设置消息为已读状态
            if (!ids.isEmpty()){
                messageService.readMessage(ids);
            }
        }

        return "/site/letter-detail";
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if (target==null){
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }

        if (StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(1, "私信内容不能为空");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (id0 == hostHolder.getUser().getId()){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

    @GetMapping("/topic/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        // 查询评论类系统通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        if (message!=null){
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);

            int unread = messageService.findUnReadNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVo);

        // 查询点赞类系统通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<>();
        if (message!=null){
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);

            int unread = messageService.findUnReadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVo);


        // 查询关注类系统通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        if (message!=null){
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);

            int unread = messageService.findUnReadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVo);

        // 查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnReadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "site/notice";
    }


    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        page.setLimit(10);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList!=null){
            for (Message notice:noticeList){
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                // 通知来源
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }

            // 设置消息已读
            List<Integer> ids = noticeList.stream()
                    .filter((t) -> (t.getStatus() == 0) && (t.getToId().equals(hostHolder.getUser().getId())))
                    .map(Message::getId)
                    .collect(Collectors.toList());
            if (!ids.isEmpty()){
                messageService.readMessage(ids);
            }
        }
        model.addAttribute("notices", noticeVoList);

        return "/site/notice-detail";
    }
}
