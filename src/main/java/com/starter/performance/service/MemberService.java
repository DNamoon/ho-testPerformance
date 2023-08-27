package com.starter.performance.service;

import com.starter.performance.controller.dto.LoginRequestDto;
import com.starter.performance.controller.dto.SignUpRequestDto;
import com.starter.performance.service.dto.LoginResponseDto;
import com.starter.performance.service.dto.SignUpResponseDto;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {

    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);
}
