package com.mb.dto.Book.resp;

import com.mb.domain.Book;
import com.mb.util.RentBookAdminLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DashboardResponseDto {

    private List<Book> recentBookList;

    private List<Book> popularBookList;

    private List<RentBookAdminLog> rentBookList;
}
