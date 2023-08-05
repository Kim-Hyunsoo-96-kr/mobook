package com.mb.dto;

import com.mb.domain.Book;
import com.mb.util.BookLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyBookResponseDto {
    private List<BookLog> bookLogList;
    private List<Book> rentBook;
    /**Todo 좋아요한 책 목록*/
//    private List<Book> likeBook;
}

