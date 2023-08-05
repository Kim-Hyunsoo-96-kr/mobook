package com.mb.util;

import lombok.Getter;

@Getter
public class BookLogUtil {
    private String bookName;
    private String bookNumber;
    private String bookStatus;
    private String regDate;

    public BookLogUtil(String bookName, String bookNumber, String bookStatus, String regDate) {
        this.bookName = bookName;
        this.bookNumber = bookNumber;
        this.bookStatus = bookStatus;
        this.regDate = regDate;
    }
}
