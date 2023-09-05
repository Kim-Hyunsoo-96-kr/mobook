package com.mb.dto;

import com.mb.domain.Notice;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NoticeListResponseDto {
    private List<Notice> noticeList;

    private Integer totalCnt;
}
