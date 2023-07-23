package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberLoginDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
    @Schema(description = "사용자 이메일", nullable = false, example = "joohwan9607@mobility42.io")
    private String email;

    @NotBlank(message = "암호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "암호는 영문, 특수문자 8자 이상 20자 이하입니다.") // 영문, 특수문자 8자 이상 20자 이하
    @Schema(description = "사용자 암호", nullable = false, example = "abcde12345!")
    private String password;
}
