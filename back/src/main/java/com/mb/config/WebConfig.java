package com.mb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // http://localhost:3000 ---> 8080 api를 호출할 수 있도록 설정.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/", "http://mobook.mobility42.io")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "OPTIONS", "DELETE")
                .allowCredentials(true);
    }
}
