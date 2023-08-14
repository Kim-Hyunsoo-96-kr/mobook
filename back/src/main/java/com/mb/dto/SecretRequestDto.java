package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecretRequestDto {
    @NotBlank(message = "암호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "암호는 영문, 특수문자 8자 이상 20자 이하입니다.") // 영문, 특수문자 8자 이상 20자 이하
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    private String name;
    private String secretKey;
}
