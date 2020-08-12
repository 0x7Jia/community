package me.echo.community.controller;

import me.echo.community.annotation.LoginRequired;
import me.echo.community.entity.User;
import me.echo.community.service.UserService;
import me.echo.community.util.CommunityUtil;
import me.echo.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

  @Value("${server.servlet.context-path}")
  private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    /**
     * 获取用户设置页面
     */
    @GetMapping("/setting")
    @LoginRequired
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 更新用户头像
     * @param headerImage 浏览器传递的头像
     */
    @PostMapping("/upload")
    @LoginRequired
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage==null){
            model.addAttribute("uploadError", "您还没有选择图片!");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("uploadError", "文件格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        String fileName = CommunityUtil.generateUUID()+suffix;
        File dest = new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常", e);
        }

        // 更新当前用户的头像路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 返回用户头像给客户端
     * @param fileName 浏览器传递的头像名称
     * @param response 以流的方式向客户端传输二进制数据
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存储路径
        fileName = uploadPath+"/"+fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try(FileInputStream fis = new FileInputStream(fileName);
            OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer))!=-1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取文件失败:"+e.getMessage());
        }
    }

    /**
     * 用户修改密码
     */
    @PostMapping("/updatePassword")
    @LoginRequired
    public String updatePassword(String oldPassword, String newPassword, String confirmationPassword, Model model){
        if (StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordError", "原始密码不能为空!");
            return "/site/setting";
        }

        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordError", "新密码不能为空!");
            return "/site/setting";
        }

        if (!newPassword.equals(confirmationPassword)){
            model.addAttribute("confirmationPasswordError", "两次输入的密码不一致!");
            return "/site/setting";
        }

        // 确定原始密码输入正确
        User user = hostHolder.getUser();
        String salt = user.getSalt();
        String password = user.getPassword();

        if (password.equals(CommunityUtil.md5(oldPassword+salt))){
            userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword+salt));
            hostHolder.clear();
            return "forward:/logout";
        }else {
            model.addAttribute("oldPasswordError", "您输入的原始密码错误!");
            return "/site/setting";
        }
    }
}
