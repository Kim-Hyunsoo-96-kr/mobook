package com.mb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReturnResponseDto {
    private String memberName;
    private String bookName;
    private String message;
}
