package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BookAddDto {
    @NotBlank(message = "책 제목을 최소 한 글자 이상 입력해주세요.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    @Schema(description = "책 제목", nullable = false, example = "아토믹 코틀린")
    private String bookName;

    @NotBlank(message = "책 넘버링을 입력해주세요.")
    private String bookNumber;
}
