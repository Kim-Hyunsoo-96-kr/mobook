package com.mb.util;

import lombok.Getter;

@Getter
public class RequestBookLog {
    private String bookName;
    private String bookLink;
    private String requestDate;
    private String completeDate;
    private String status;

    public RequestBookLog(String bookName, String bookLink,  String requestDate, String completeDate, String status) {
        this.bookName = bookName;
        this.bookLink = bookLink;
        this.requestDate = requestDate;
        this.completeDate = completeDate;
        this.status = status;
    }
}
