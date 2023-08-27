package com.starter.performance.security;

import com.starter.performance.service.MemberService;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader("Authorization");
        log.info("authorization: {}", authorization);

        //토큰이 없거나 Bearer 토큰이 아닐 경우 접근 불가
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("잘못된 authorization");
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 꺼내기
        String token = authorization.split(" ")[1];

        //토큰이 만료되었는지 검증
        if (JwtUtil.isExpired(token, secretKey)) {
            log.error("만료된 토큰");
            filterChain.doFilter(request, response);
            return;
        }

//        토큰에서 email 꺼내기
        String email = JwtUtil.getEmail(token, secretKey);
        log.info("email: {}", email);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, null,
                List.of(new SimpleGrantedAuthority("USER")));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}