package com.mb.dto.Book.resp;

import com.mb.domain.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRecommendLogResponseDto {
    private List<Book> recommendBook;

    private Integer totalCnt;

}

