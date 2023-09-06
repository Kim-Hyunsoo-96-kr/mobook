package com.mb.dto.Book.resp;

import com.mb.domain.Book;
import com.mb.util.BookLogUtil;
import com.mb.util.RentBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookLogResponseDto {
    private List<BookLogUtil> bookLogList;

    private Integer totalCnt;
}

