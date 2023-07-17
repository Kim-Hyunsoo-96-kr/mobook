package com.mb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BookSearchDto {
    @NotNull(message = "null값을 제외한 입력값이어야만 합니다.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    private String searchText;
    @NotNull(message = "null값을 제외한 입력값이어야만 합니다.")
    @PositiveOrZero(message = "0 또는 양수를 입력해주세요.")
    private Integer page;
}
