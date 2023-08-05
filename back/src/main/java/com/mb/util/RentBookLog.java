package com.mb.util;

import lombok.Getter;

@Getter
public class RentBookLog {
    private String bookNumber;
    private String bookName;
    private Integer recommend;
    private String rentDate;
    private String returnDate;

    public RentBookLog(String bookNumber, String bookName, Integer recommend, String rentDate, String returnDate) {
        this.bookNumber = bookNumber;
        this.bookName = bookName;
        this.recommend = recommend;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
    }
}
