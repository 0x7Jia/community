package me.echo.community.config;

import me.echo.community.util.CommunityConstant;
import me.echo.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 忽略静态资源
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/updatePassword",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN, AUTHORITY_MODERATOR, AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                        )
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                .and().csrf().disable();

        // 权限不足时处理
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            // 未登录处理
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
                        throws IOException, ServletException {
                    String xRequestWith = request.getHeader("X-Requested-With");
                    if ("XMLHttpRequest".equals(xRequestWith)){
                        // 异步
                        response.setContentType("application/plain;charset=utf8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "你还没有登录!"));
                    }else {
                        response.sendRedirect(request.getContextPath()+"/login");
                    }
                }
            }).accessDeniedHandler(new AccessDeniedHandler() {
                // 权限不足处理
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
                        throws IOException, ServletException {
                    String xRequestWith = request.getHeader("X-Requested-With");
                    if ("XMLHttpRequest".equals(xRequestWith)){
                        // 异步
                        response.setContentType("application/plain;charset=utf8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                    }else {
                        response.sendRedirect(request.getContextPath()+"/denied");
                    }
                }
            });

        // 执行自己的logout逻辑 指向一个不存在的地址让spring security拦截
        http.logout().logoutUrl("/securityLogout");
    }
}
