package com.mb.service;

import com.mb.domain.Book;
import com.mb.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    public Book saveBook(Book newBook) {
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

    public List<Book> getBookListByKeyword(String keyword, Pageable pageable) {
        List<Book> bookList = bookRepository.findByBookNameContaining(keyword, pageable);
        return bookList;
    }

    public List<Book> findByRentalMemberId(Long memberId) {
        List<Book> bookList = bookRepository.findByRentalMemberId(memberId);
        return bookList;
    }

    public void addBookByExcel(List<Book> list) {
        for (Book book : list) {
            bookRepository.save(book);
        }
    }

    public Book findByBookNumber(String bookNumber) {
        Book findBook = bookRepository.findByBookNumber(bookNumber).orElseThrow(()-> new IllegalArgumentException("등로되지 않은 책입니다."));
        return findBook;
    }

    public Integer getTotalCntBySearchText(String searchText) {
        List<Book> bookList = bookRepository.findByBookNameContaining(searchText);
        return bookList.size();
    }
}
