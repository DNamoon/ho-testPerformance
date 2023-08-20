package com.starter.performance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Spring Security 사용하기 위해서 설정파일 있어야 함 */
@Configuration
public class SecurityConfig {
    //  비밀번호 암호화 (단방향)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().disable()			//cors 방지
            .csrf().disable()			//csrf 방지
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            //Security가 세션을 생성 못하게 막음. 기존엔 항시 생성
            .and()
            .authorizeRequests()
            .antMatchers("/review/**","/").permitAll() // /auth이하의 주소들은 인증 필요x
            .antMatchers("/css/**","/js/**").permitAll()
            .anyRequest().authenticated()
            //권한을 확인하는 과정에서 통과하지 못하는 예외가 발생할 경우 예외 전달 - CustomAccessDeniedHandler() 클래스 작성 요망
            //.and()
            //.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            // 인증 과정에서 예외가 발생할 경우 예외를 전달 - CustomAuthenticationEntryPoint 클래스 작성 요망
            //.and()
            //.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
            .headers().frameOptions().disable();
        return http.build();
    }

}

