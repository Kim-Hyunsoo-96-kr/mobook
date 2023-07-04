package com.mb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginResponseDto {
    private String name;
    private String accessToken;
    private String refreshToken;
}
