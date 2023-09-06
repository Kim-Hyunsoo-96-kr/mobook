package com.mb.dto.Book.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BookRequestDto {
    @NotBlank(message = "책 제목을 최소 한 글자 이상 입력해주세요.")
    @Size(max = 100, message = "100자 이하로 입력해주세요.")
    @Schema(description = "책 제목", nullable = false, example = "아토믹 코틀린")
    private String bookName;

    private String bookPublisher;

    private String bookWriter;
    public BookRequestDto() {
        // 필드를 원하는 디폴트 값으로 초기화할 수 있습니다.
        this.bookPublisher = "빈 값";
        this.bookWriter = "빈 값";
    }
}
