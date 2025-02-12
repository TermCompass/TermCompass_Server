package com.aivle.TermCompass.config;

import com.aivle.TermCompass.filter.JwtAuthenticationFilter;
import com.aivle.TermCompass.service.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring-host}")
    private String hostname;

    private final JwtTokenProvider jwtTokenProvider;

    public WebConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("https://" + hostname)
                .allowedOrigins("http://admin." + hostname)
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
}

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}
