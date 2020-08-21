package me.echo.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan("me.echo.community.dao")
public class CommunityApplication {

    @PostConstruct
    public void init(){
        // org.elasticsearch.transport.netty4.Netty4Utils.setAvailableProcessors
       System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
