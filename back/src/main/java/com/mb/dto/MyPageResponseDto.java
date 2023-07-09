package com.mb.dto;

import com.mb.domain.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageResponseDto {
    private String name;
    private String email;
    private Boolean isAdmin;
    private List<Book> rentalBookList;
}
