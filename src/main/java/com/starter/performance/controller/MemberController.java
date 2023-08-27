package com.starter.performance.controller;

import com.starter.performance.controller.dto.LoginRequestDto;
import com.starter.performance.controller.dto.SignUpRequestDto;
import com.starter.performance.service.MemberService;
import com.starter.performance.service.dto.ResponseDto;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return new ResponseEntity<>(
            ResponseDto.builder()
                .statusCode("201")
                .message("회원가입 성공")
                .data(memberService.signUp(signUpRequestDto))
                .build(),
//            new ResponseDto(
//                "201",
//                "회원가입 성공",
//                memberService.signUp(signUpRequestDto)
//            ),
            HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto,
        HttpServletResponse response) {
        return new ResponseEntity<>(
            ResponseDto.builder()
                .statusCode("200")
                .message("로그인 성공")
                .data(memberService.login(loginRequestDto, response)),
//            new ResponseDto(
//                "200",
//                "로그인 성공",
//                memberService.login(loginRequestDto, response)
//            ),
            HttpStatus.OK
        );
    }

//    리뷰를 등록한 사용자의 이메일을 잘 받아오는지 확인하기 위한 임시 코드
//    @PostMapping("/review")
//    public ResponseEntity<String> review(Authentication authentication) {
//        return ResponseEntity.ok().body(authentication.getName() + "의 리뷰 등록 완료");
//    }
}