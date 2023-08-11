package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberLoginDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Schema(description = "사용자 이메일", nullable = false, example = "joohwan9607@mobility42.io")
    private String email;

    @NotBlank(message = "암호를 입력해주세요.")
    @Schema(description = "사용자 암호", nullable = false, example = "abcde12345!")
    private String password;
}
