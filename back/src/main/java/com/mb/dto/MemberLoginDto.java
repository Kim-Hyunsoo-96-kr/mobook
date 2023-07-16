package com.mb.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberLoginDto {
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
    private String email;
    @NotEmpty(message = "암호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "암호는 영문, 특수문자 8자 이상 20자 이하입니다.") // 영문, 특수문자 8자 이상 20자 이하
    private String password;
}
