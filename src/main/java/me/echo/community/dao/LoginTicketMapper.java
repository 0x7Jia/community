package me.echo.community.dao;

import me.echo.community.entity.LoginTicket;

public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectLoginTicket(String ticket);

    int updateStatus(String ticket, Integer status);
}
