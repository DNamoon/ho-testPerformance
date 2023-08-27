package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.LoginRequestDto;
import com.starter.performance.controller.dto.SignUpRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.exception.CustomException;
import com.starter.performance.exception.ErrorType;
import com.starter.performance.exception.dto.ErrorDataDto;
import com.starter.performance.exception.dto.ErrorResponseDto;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.security.JwtUtil;
import com.starter.performance.service.MemberService;
import com.starter.performance.service.dto.LoginResponseDto;
import com.starter.performance.service.dto.SignUpResponseDto;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private Long expirationTime = 1000 * 60 * 60L;

    @Override
    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        memberRepository.findByEmail(signUpRequestDto.getEmail())
            .ifPresent(member -> {
                throw new CustomException(
                    new ErrorResponseDto("409",
                        new ErrorDataDto(ErrorType.EMAIL_IS_DUPLICATED_EXCEPTION.getErrorType())
                    ),
                    HttpStatus.CONFLICT
                );
            });

        memberRepository.findByNickname(signUpRequestDto.getNickname())
            .ifPresent(member -> {
                throw new CustomException(
                    new ErrorResponseDto("409",
                        new ErrorDataDto(ErrorType.NICKNAME_IS_DUPLICATED_EXCEPTION.getErrorType())
                    ),
                    HttpStatus.CONFLICT
                );
            });

        Member member = signUpRequestDto.toEntity(bCryptPasswordEncoder);
        memberRepository.save(member);

        return SignUpResponseDto.builder()
            .email(member.getEmail())
            .password(member.getPassword())
            .nickname(member.getNickname())
            .phoneNumber(member.getPhoneNumber())
            .build();
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
            .orElseThrow(() -> new CustomException(
                new ErrorResponseDto("404",
                    new ErrorDataDto(ErrorType.UNSUBSCRIBED_EMAIL_EXCEPTION.getErrorType())
                ),
                HttpStatus.NOT_FOUND
            ));

        if (!bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(
                new ErrorResponseDto("409",
                    new ErrorDataDto(ErrorType.WRONG_PASSWORD_EXCEPTION.getErrorType())
                ),
                HttpStatus.CONFLICT
            );
        }

        String token = JwtUtil.createToken(
            member.getId(), member.getEmail(), secretKey, expirationTime);

        response.addHeader("Authorization", "Bearer " + token);

        return LoginResponseDto.builder()
            .email(member.getEmail())
            .password(member.getPassword())
            .build();
    }
}