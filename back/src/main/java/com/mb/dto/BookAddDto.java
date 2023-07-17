package com.mb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BookAddDto {
    @NotBlank(message = "책 제목을 최소 한 글자 이상 입력해주세요.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    private String name;
}
