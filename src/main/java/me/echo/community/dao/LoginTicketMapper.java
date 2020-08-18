package me.echo.community.dao;

import me.echo.community.entity.LoginTicket;

@Deprecated
public interface LoginTicketMapper {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectLoginTicket(String ticket);

    LoginTicket selectLoginTicketById(Integer id);

    int updateStatus(String ticket, Integer status);

    int updateLoginTicket(LoginTicket loginTicket);
}
