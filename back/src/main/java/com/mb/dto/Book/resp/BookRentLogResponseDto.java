package com.mb.dto.Book.resp;

import com.mb.util.RentBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRentLogResponseDto {
    private List<RentBookLog> rentBook;

    private Integer totalCnt;
}

