package com.swd392.mentorbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các endpoint
                .allowedOrigins("http://localhost:3000",
                        "http://localhost:8080",
                        "http://localhost:5173",
                        "http://167.71.220.5:8080",
                        "https://circuit-project.vercel.app/") // Thêm các domain được phép truy cập
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Cho phép các phương thức
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true); // Cho phép gửi thông tin xác thực (cookie, auth headers)

        // Thêm cấu hình cho Swagger
        registry.addMapping("/v2/api-docs/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registry.addMapping("/swagger-resources/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registry.addMapping("/swagger-ui.html").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registry.addMapping("/webjars/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registry.addMapping("/swagger-ui/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
