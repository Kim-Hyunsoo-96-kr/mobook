package com.mb.util;

import lombok.Getter;

@Getter
public class RequestBookAdminLog {
    private String bookName;
    private String requestDate;
    private String completeDate;
    private String status;
    private String userName;

    public RequestBookAdminLog(String bookName, String requestDate, String completeDate, String status, String userName) {
        this.bookName = bookName;
        this.requestDate = requestDate;
        this.completeDate = completeDate;
        this.status = status;
        this.userName = userName;
    }
}
