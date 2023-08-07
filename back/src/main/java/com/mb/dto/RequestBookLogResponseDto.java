package com.mb.dto;

import com.mb.util.RequestBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestBookLogResponseDto {
    private List<RequestBookLog> requestBookLogList;
}

