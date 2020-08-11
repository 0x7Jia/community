package me.echo.community.util;

import me.echo.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户的信息
 */
@Component
public class HostHolder {
    private final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void setUser(User user){
        threadLocal.set(user);
    }

    public User getUser(){
        return threadLocal.get();
    }

    public void clear(){
        threadLocal.remove();
    }
}

