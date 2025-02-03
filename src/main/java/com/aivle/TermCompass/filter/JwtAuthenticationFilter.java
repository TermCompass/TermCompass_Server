package com.aivle.TermCompass.filter;

import com.aivle.TermCompass.service.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(request); // 헤더 또는 쿠키에서 토큰 추출
            System.out.println("Extracted Token: " + token);

            if (token != null) {
                Claims claims = jwtTokenProvider.validateToken(token);

                if (claims != null) {
                    // 토큰이 유효하면 인증 정보 생성
                    String email = claims.getSubject(); // 토큰의 subject(email) 추출
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // 기본 권한 설정

                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);


                    System.out.println("Decoded Email: " + email);
                    System.out.println("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

                    // 요청 속성에 email 추가 (추후 컨트롤러에서 사용 가능)
                    request.setAttribute("email", email);
                }
            }
        } catch (Exception e) {
            logger.error("JWT 인증 중 오류 발생: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response); // 필터 체인 진행
    }

    /**
     * HTTP 헤더 또는 쿠키에서 JWT 토큰을 추출
     */
    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
