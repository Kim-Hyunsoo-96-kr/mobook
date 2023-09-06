package com.mb.dto.Admin.resp;

import com.mb.util.RentBookAdminLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminRentBookLogResponseDto {
    private List<RentBookAdminLog> rentBook;
    private Integer totalCnt;
}

