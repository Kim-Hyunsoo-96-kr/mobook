package com.mb.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDto {
    @NotEmpty(message = "null 값이거나 빈 문자열은 올 수 없습니다.")
    private String refreshToken;
}
