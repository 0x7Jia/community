package me.echo.community.controller;

import me.echo.community.entity.Message;
import me.echo.community.entity.Page;
import me.echo.community.entity.User;
import me.echo.community.service.MessageService;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController {

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
}
