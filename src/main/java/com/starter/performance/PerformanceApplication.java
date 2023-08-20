package com.starter.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 따로 깃 연동해 놓은 프로젝트. 내 개인 git에 ho-testPerformance 레퍼지토리와 연결.
 * 팀 프로젝트 끝나고 따로 개인프로젝트 연동해서 사용하는 방법이라고 함.
 * https://velog.io/@yeoonnii/Git-%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%86%8C%EC%8A%A4%EC%BD%94%EB%93%9C-%EA%B0%9C%EC%9D%B8-Repository%EB%A1%9C-%EC%98%AE%EA%B8%B0%EA%B8%B0
 * */
@SpringBootApplication
public class PerformanceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PerformanceApplication.class, args);
  }

}
