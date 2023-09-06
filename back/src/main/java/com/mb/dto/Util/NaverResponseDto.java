package com.mb.dto.Util;

import com.mb.util.NaverBook;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NaverResponseDto {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NaverBook> items;
}
