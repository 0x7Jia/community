package me.echo.community.util;

/**
 * 定义常量
 */
public interface CommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 激活失败
    int ACTIVATION_FAILURE = 1;

    // 重复激活
    int ACTIVATION_REPEAT = 2;

    // 默认状态下最大登录有效期
    int DEFAULT_EXPIRED_SECONDS = 3600*24;

    // 记住我状态下最大登录有效期
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100;

    // 实体类型 帖子
    int ENTITY_TYPE_POST = 1;

    // 实体类型 评论
    int ENTITY_TYPE_COMMENT = 2;

    // 实体类型 用户
    int ENTITY_TYPE_USER = 3;

    int SYSTEM_USER_ID = 1;

    // 主题 评论
    String TOPIC_COMMENT = "comment";

    // 主题 点赞
    String TOPIC_LIKE = "like";

    // 主题 关注
    String TOPIC_FOLLOW = "follow";

    // 主题 发帖
    String TOPIC_PUBLISH = "publish";
}
