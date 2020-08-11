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
}
