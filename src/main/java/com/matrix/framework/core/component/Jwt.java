package com.matrix.framework.core.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
/**
 * JWT工具类
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/10/3 11:15
 * @Since 1.0
 */
@Component
public class Jwt {

    // 使用 Keys.secretKeyFor(SignatureAlgorithm.HS256) 来生成一个足够安全的密钥
    public static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成JWT
    public String generateToken(String id, String username) {
        // 60分钟过期
        long JWT_EXPIRATION = 1000 * 60 * 60;
        return Jwts.builder()
                .setId(id)
                .setSubject(username)  // 设置用户名
                .setIssuedAt(new Date())  // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // 设置过期时间
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 使用HS256算法签名
                .compact();  // 生成JWT
    }

    // 验证Token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 提取用户名
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getId();
    }

    // 检查Token是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 提取过期时间
    public Date extractExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }
}

