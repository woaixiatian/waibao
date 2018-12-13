package com.bilibili;

import com.bilibili.net.netty.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IncarnationApplication {
    public static void main(String[] args) {
        SpringApplication.run(IncarnationApplication.class, args);
        //启动netty服务端
        new Server().start();
    }
}
