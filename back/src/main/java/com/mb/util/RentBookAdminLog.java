package com.mb.util;

import lombok.Getter;

@Getter
public class RentBookAdminLog {
    private String bookNumber;
    private String bookName;
    private Integer recommend;
    private String rentDate;
    private String returnDate;
    private String userName;

    public RentBookAdminLog(String bookNumber, String bookName, Integer recommend, String rentDate, String returnDate, String userName) {
        this.bookNumber = bookNumber;
        this.bookName = bookName;
        this.recommend = recommend;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.userName = userName;
    }
}
