package me.echo.community.service;

import me.echo.community.dao.UserMapper;
import me.echo.community.entity.User;
import me.echo.community.enums.UserStatus;
import me.echo.community.enums.UserType;
import me.echo.community.util.CommunityConstant;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired(required = false)
    private UserMapper userMapper;

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        if (StringUtils.isBlank(user.getUserName())) {
            map.put("usernameError", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailError", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordError", "密码不能为空");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(UserType.ORDINARY.getKey());
        user.setStatus(UserStatus.INACTIVATED.getKey());
        user.setActivationCode(CommunityUtil.generateUUID());
//        user.setHeaderUrl();
        user.setCreateTime(new Date());

        userMapper.saveUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/id/code
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 激活账号
     * @param userId 用户id主键
     * @param code 激活码
     * @return 激活状态
     */
    public int activation(int userId, String code){
        User user = userMapper.selectUserById(userId);
        if (user.getStatus()==UserStatus.ACTIVATED.getKey()){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, UserStatus.ACTIVATED.getKey());
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }
}
