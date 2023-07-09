package com.mb.service;

import com.mb.domain.RefreshToken;
import com.mb.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    public void addToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findRefreshToken(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByValue(refreshToken).orElseThrow(()-> new IllegalArgumentException("등록되지 않은 RefreshToken입니다."));
        return findRefreshToken;
    }
}
