package indi.mofan.order.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author mofan
 * @date 2025/3/23 17:58
 */
@Configuration
public class ProductServiceConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
