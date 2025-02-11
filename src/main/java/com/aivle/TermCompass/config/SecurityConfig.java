package com.aivle.TermCompass.config;

import com.aivle.TermCompass.filter.JwtAuthenticationFilter;
import com.aivle.TermCompass.service.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring-host}")
    private String hostname;

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    // 여기서 '*'를 사용하지 않고, 특정 origin을 명시적으로 설정
                    config.addAllowedOrigin("https://" + hostname + ":8000");
                    config.setAllowCredentials(true);  // 쿠키 전송을 허용
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/","/login", "/signup").permitAll() // 인증 없이 접근 가능
                        .requestMatchers("/change-password").authenticated() // JWT 인증 필요
                        .anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 추가
                .formLogin(AbstractHttpConfigurer::disable)
                .logout((logout) ->
                        logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/logout-success")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .addLogoutHandler((request, response, authentication) -> {
                                    // JWT 쿠키 삭제 처리
                                    Cookie cookie = new Cookie("jwt", "");
                                    cookie.setPath("/");  // 쿠키 경로 설정
                                    cookie.setMaxAge(0);  // 쿠키 만료 시간 설정
                                    response.addCookie(cookie);
                                })
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
