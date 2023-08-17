package com.mb.dto;

import com.mb.domain.Book;
import com.mb.util.BookLogUtil;
import com.mb.util.RentBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendBookLogResponseDto {
    private List<Book> recommendBook;

    private Integer totalCnt;

}

