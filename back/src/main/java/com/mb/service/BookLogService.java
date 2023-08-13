package com.mb.service;


import com.mb.domain.Book;
import com.mb.domain.BookLog;
import com.mb.domain.Member;
import com.mb.enum_.BookStatus;
import com.mb.repository.BookLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLogService {
    private final BookLogRepository bookLogRepository;
    public void addBookMember(BookLog bookHistory) {
        bookLogRepository.save(bookHistory);
    }

    public List<BookLog> findBookLogByMemberId(Member member) {
        List<BookLog> list =  bookLogRepository.findByMember(member);
        return list;
    }

    public BookLog findByMemberAndBookAndStatus(Member member, Book book, BookStatus status) {
        BookLog findBookHistory =  bookLogRepository.findByMemberAndBookAndStatus(member, book, status.getBookStatus()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 로그입니다."));
        return findBookHistory;
    }

    public List<BookLog> findByMemberAndStatus(Member member, BookStatus status) {
        List<BookLog> bookLogList =  bookLogRepository.findByMemberAndStatus(member, status.getBookStatus());
        return bookLogList;
    }

    public List<BookLog> findAll() {
        List<BookLog> all = bookLogRepository.findAll();
        return all;
    }
}
