package com.mb.enum_;

public enum BookStatus {
    Rent("대여"), Return("반납"), InRental("대여 중"), Request("요청"), RequestComplete("처리 완료");
    final private String bookStatus;

    BookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookStatus() {
        return bookStatus;
    }
}
