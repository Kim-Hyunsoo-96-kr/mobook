package com.mb.dto;

import com.mb.util.BookLogAdminUtil;
import com.mb.util.BookLogUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminBookLogResponseDto {
    private List<BookLogAdminUtil> bookLogList;
    private Integer totalCnt;
}

