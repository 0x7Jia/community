package me.echo.community.controller;


import com.google.code.kaptcha.Producer;
import me.echo.community.entity.User;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("server.servlet.context-path")
    private String contentPath;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }


    /**
     * 注册账号
     * @param user 前端封装好的数据对应 entity 中的 User 属性， 自动封装入 Model
     * @return 创建账号成功: 跳转到操作结果页面
     *         创建账号失败: 携带失败原因跳转到注册页面
     */
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);

        if (map==null||map.isEmpty()){
            // 成功
            model.addAttribute("msg", "您的账户创建成功，请查看您的邮件进行账户激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameError"));
            model.addAttribute("passwordMsg", map.get("emailError"));
            model.addAttribute("emailMsg", map.get("passwordError"));

            return "/site/register";
        }
    }

    /**
     * 激活账号 url格式: http://localhost:8080/community/activation/id/code
     * @param userId 路径变量获取 对应用户的主键
     * @param code 路径变量获取 对应激活码
     * @return 激活成功: 跳转至登录页面 可以进行的登录
     *         激活失败: 跳转到首页 并携带操作结果
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result==ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        }else if (result==ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效的操作，该账号已经激活!");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码 返回给客户端
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 保存验证码文字到session
        session.setAttribute("kaptcha", text);

        // 返回验证码图片
        response.setContentType("image/png");
        try{
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        }catch (IOException e){
            // ignore
            logger.error("验证码响应失败:"+e.getMessage());
        }
    }

    /**
     *
     * @param userName
     * @param password
     * @param code
     * @param rememberMe
     * @param model
     * @param session
     * @param response
     * @return
     */
    @PostMapping("/login")
    public String login(String userName, String password, String code, boolean rememberMe, Model model,
                        HttpSession session, HttpServletResponse response){

        // 检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 检查账号 密码
        int expireSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(userName, password, expireSeconds);
        if (map.containsKey("ticket")){
            // 登录成功 设置 cookie
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contentPath);
            cookie.setMaxAge(expireSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("userNameMsg", map.get("userNameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
