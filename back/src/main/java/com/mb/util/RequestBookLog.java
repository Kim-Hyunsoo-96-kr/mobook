package com.mb.util;

import lombok.Getter;

@Getter
public class RequestBookLog {
    private String bookName;
    private String requestDate;
    private String completeDate;
    private String status;

    public RequestBookLog(String bookName, String requestDate, String completeDate, String status) {
        this.bookName = bookName;
        this.requestDate = requestDate;
        this.completeDate = completeDate;
        this.status = status;
    }
}
