package me.echo.community.service;

import me.echo.community.dao.MessageMapper;
import me.echo.community.entity.Message;
import me.echo.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired(required = false)
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(Integer userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(Integer userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(Integer userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids, 1);
    }


    /**
     * 查询最新的主题通知
     * @param userId
     * @param topic
     * @return
     */
    public Message findLatestNotice(Integer userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }


    /**
     * 查询主题下的消息总数
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(Integer userId, String topic){
        return messageMapper.selectNoticeCount(userId, topic);
    }


    /**
     * 查询主题中所有未读的消息总数
     * @param userId
     * @param topic null 则查询总和未读
     * @return
     */
    public int findUnReadNoticeCount(Integer userId, String topic){
        return messageMapper.selectUnReadNoticeCount(userId, topic);
    }


    /**
     * 查询主题消息详情
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findNotices(Integer userId, String topic, int offset, int limit){
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
