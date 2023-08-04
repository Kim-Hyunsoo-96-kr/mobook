package com.mb.util;

import lombok.Getter;

@Getter
public class BookLog {
    private String bookName;
    private String bookNumber;
    private String bookStatus;

    public BookLog(String bookName, String bookNumber, String bookStatus) {
        this.bookName = bookName;
        this.bookNumber = bookNumber;
        this.bookStatus = bookStatus;
    }
}
