package com.mb.service;


import com.mb.domain.Book;
import com.mb.domain.BookLog;
import com.mb.domain.Member;
import com.mb.domain.QBookLog;
import com.mb.enum_.BookStatus;
import com.mb.repository.BookLogRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    public List<BookLog> findByStatus(BookStatus bookStatus) {
        List<BookLog> bookLogList =  bookLogRepository.findByStatus(bookStatus.getBookStatus());
        return bookLogList;
    }

    public List<BookLog> findBookLogByMemberAndKeyword(Member loginMember, String searchText, Pageable pageable) {
        QBookLog qBookLog = QBookLog.bookLog;

        BooleanExpression predicate = qBookLog.member.eq(loginMember)
                .and(qBookLog.book.bookName.containsIgnoreCase(searchText));
        List<BookLog> bookLogList = bookLogRepository.findAll(predicate, pageable).getContent();
        return bookLogList;
    }

    public Integer getBookLogByMemberAndKeywordCnt(Member loginMember, String searchText) {
        QBookLog qBookLog = QBookLog.bookLog;

        BooleanExpression predicate = qBookLog.member.eq(loginMember)
                .and(qBookLog.book.bookName.containsIgnoreCase(searchText));
        List<BookLog> bookLogList = (List<BookLog>) bookLogRepository.findAll(predicate);
        return bookLogList.size();
    }
}
