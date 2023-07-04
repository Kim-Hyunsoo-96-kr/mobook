package com.mb.util;

import com.mb.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    public final static Long accessTokenExpiredMs = 60 * 60 * 1000L; //1시간
    public final static Long refreshTokenExpiredMs = 7 * 24 * 60 * 60 * 1000L; //7일

    public static String createAccessToken(Member member, String accessSecretKey){
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getMemberId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    public static String createRefreshToken(Member member, String refreshSecretKey) {
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getMemberId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();
    }
}
