package com.mb.dto.Book.resp;

import com.mb.domain.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookListResponseDto {
    private List<Book> bookList;

    private Integer totalCnt;
}
