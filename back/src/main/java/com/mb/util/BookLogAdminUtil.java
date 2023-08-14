package com.mb.util;

import lombok.Getter;

@Getter
public class BookLogAdminUtil {
    private String bookName;
    private String bookNumber;
    private String bookStatus;
    private String regDate;
    private String userName;

    public BookLogAdminUtil(String bookName, String bookNumber, String bookStatus, String regDate, String userName) {
        this.bookName = bookName;
        this.bookNumber = bookNumber;
        this.bookStatus = bookStatus;
        this.regDate = regDate;
        this.userName = userName;
    }
}
