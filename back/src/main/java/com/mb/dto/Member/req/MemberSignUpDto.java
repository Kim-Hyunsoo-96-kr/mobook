package com.mb.dto.Member.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberSignUpDto {
    @NotBlank(message = "암호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "암호는 영문, 특수문자 8자 이상 20자 이하입니다.") // 영문, 특수문자 8자 이상 20자 이하
    @Schema(description = "사용자 암호", nullable = false, example = "abcde12345!")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 아닙니다.")
    @Schema(description = "사용자 이메일", nullable = false, example = "joohwan9607@mobility42.io")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    @Schema(description = "사용자 이름", nullable = false, example = "송주환")
    private String name;

}
