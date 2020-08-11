package me.echo.community.dao;

import me.echo.community.entity.User;

public interface UserMapper {
    /**
     * 根据id查询用户
     */
    User selectUserById(Integer id);

    /**
     * 根据用户名查询用户
     */
    User selectUserByUserName(String userName);

    /**
     * 保存用户，操作会将自动生成的id保存到 User 对象中
     */
    int saveUser(User user);

    /**
     * 更新用户的账号状态， 激活账号 冻结账号
     */
    void updateStatus(Integer userId, Integer status);

}
