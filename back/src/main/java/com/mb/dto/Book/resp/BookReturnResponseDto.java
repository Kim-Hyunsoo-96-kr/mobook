package com.mb.dto.Book.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReturnResponseDto {
    private String memberName;
    private String bookName;
    private String message;
}
