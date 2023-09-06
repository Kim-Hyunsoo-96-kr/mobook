package com.mb.dto.Book.resp;

import com.mb.util.RequestBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRequestLogResponseDto {
    private List<RequestBookLog> requestBookLogList;

    private Integer totalCnt;
}

