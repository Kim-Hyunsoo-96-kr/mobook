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
}
