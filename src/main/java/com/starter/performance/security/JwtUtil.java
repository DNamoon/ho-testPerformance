package com.starter.performance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    public static String createToken(Long id, String email,
        String secretKey, long expirationTime) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("email", email);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis())) //토큰 생성일
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) //토큰 만료일
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration()
            .before(new Date());
    }

    public static String getEmail(String token, String secretKey) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .get("email", String.class);
    }

    /** 리팩토링 시 사용 */
    // 실험 중. email 꺼낸게 우연인지, 넣어줘서 꺼낼 수 있던것인지 확인.
    public static Long getId(String token, String secretKey) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .get("id", Long.class);
    }

}
