package me.echo.community.controller;


import com.google.code.kaptcha.Producer;
import me.echo.community.entity.User;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

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

    // http://localhost:8080/community/activation/id/code
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
            logger.error("验证码相应失败:"+e.getMessage());
        }
    }
}
