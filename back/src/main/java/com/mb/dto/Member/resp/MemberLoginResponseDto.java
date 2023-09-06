package com.mb.dto.Member.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginResponseDto {
    private String name;
    private String accessToken;
    private String refreshToken;
}
