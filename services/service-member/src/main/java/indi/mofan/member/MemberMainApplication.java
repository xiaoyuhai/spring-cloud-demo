package indi.mofan.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 会员服务启动类
 * @author mofan
 * @date 2025/1/15 10:00
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MemberMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberMainApplication.class, args);
    }
}