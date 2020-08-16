package me.echo.community.dao;

import me.echo.community.entity.Message;

import java.util.List;

public interface MessageMapper {
    // 查询当前用户会话列表， 每个会话需要返回一条最新的私信
    List<Message> selectConversations(Integer userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(Integer userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    int selectLetterUnreadCount(Integer userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息状态
    int updateStatus(List<Integer> ids, int status);
}
