package com.mb.dto;

import com.mb.util.RentBookAdminLog;
import com.mb.util.RentBookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RentBookAdminLogResponseDto {
    private List<RentBookAdminLog> rentBook;
}

