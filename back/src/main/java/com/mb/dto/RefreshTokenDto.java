package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDto {
    @NotEmpty(message = "null 값이거나 빈 문자열은 올 수 없습니다.")
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;
}
