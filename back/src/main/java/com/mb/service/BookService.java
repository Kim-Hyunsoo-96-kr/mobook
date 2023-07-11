package com.mb.service;

import com.mb.domain.Book;
import com.mb.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    public Book addBook(Book newBook) {
        Book saveBook = bookRepository.save(newBook);
        return saveBook;
    }

    public List<Book> getBooksList() {
        List<Book> bookList = bookRepository.findAll();
        return bookList;
    }

    public Book findById(Long bookId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("등로되지 않은 책입니다."));
        return findBook;
    }

    public List<Book> getBookListByKeyword(String keyword) {
        List<Book> bookList = bookRepository.findByBookNameContaining(keyword);
        return bookList;
    }
}
