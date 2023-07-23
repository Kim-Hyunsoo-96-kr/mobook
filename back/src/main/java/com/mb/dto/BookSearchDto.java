package com.mb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BookSearchDto {
    @NotNull(message = "null값을 제외한 입력값이어야만 합니다.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    @Schema(description = "검색어", nullable = false, example = "코틀린")
    private String searchText;

    @NotNull(message = "null값을 제외한 입력값이어야만 합니다.")
    @PositiveOrZero(message = "0 또는 양수를 입력해주세요.")
    @Schema(description = "페이지 수", nullable = false, example = "2")
    private Integer page;
}
