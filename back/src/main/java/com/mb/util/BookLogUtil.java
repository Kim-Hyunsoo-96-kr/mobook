package com.mb.util;

import lombok.Getter;

@Getter
public class BookLogUtil {
    private String bookName;
    private String bookLink;
    private String bookImageUrl;
    private String bookNumber;
    private String bookStatus;
    private String regDate;

    public BookLogUtil(String bookName, String bookLink, String bookImageUrl, String bookNumber, String bookStatus, String regDate) {
        this.bookName = bookName;
        this.bookLink = bookLink;
        this.bookImageUrl = bookImageUrl;
        this.bookNumber = bookNumber;
        this.bookStatus = bookStatus;
        this.regDate = regDate;
    }
}
