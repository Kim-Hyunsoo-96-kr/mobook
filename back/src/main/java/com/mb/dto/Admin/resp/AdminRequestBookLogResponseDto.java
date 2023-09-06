package com.mb.dto.Admin.resp;

import com.mb.util.RequestBookAdminLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminRequestBookLogResponseDto {
    private List<RequestBookAdminLog> requestBookLogList;
    private Integer totalCnt;
}

