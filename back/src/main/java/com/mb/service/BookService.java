package com.mb.service;

import com.mb.domain.Book;
import com.mb.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    public Book addBook(Book newBook) {
        Book saveBook = bookRepository.save(newBook);
        return saveBook;
    }
}
