package me.echo.community.controller.interceptor;

import me.echo.community.entity.User;
import me.echo.community.service.MessageService;
import me.echo.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    private final HostHolder hostHolder;

    private final MessageService messageService;

    public MessageInterceptor(HostHolder hostHolder, MessageService messageService) {
        this.hostHolder = hostHolder;
        this.messageService = messageService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user!=null && modelAndView!=null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findUnReadNoticeCount(user.getId(), null);

            modelAndView.addObject("totalUnreadCount", letterUnreadCount+noticeUnreadCount);
        }
    }
}
