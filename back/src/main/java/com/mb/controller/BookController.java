package com.mb.controller;

import com.mb.domain.Book;
import com.mb.dto.BookAddDto;
import com.mb.dto.BookAddResponseDto;
import com.mb.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        Book newBook = new Book();

        newBook.setBookName(bookAddDto.getName());

        Book addBook = bookService.addBook(newBook);

        BookAddResponseDto bookAddResponseDto = new BookAddResponseDto();
        bookAddResponseDto.setName(addBook.getBookName());

        return new ResponseEntity(bookAddResponseDto, HttpStatus.OK);
    }

}
