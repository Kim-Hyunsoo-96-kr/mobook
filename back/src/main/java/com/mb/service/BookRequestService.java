package com.mb.service;


import com.mb.domain.*;
import com.mb.repository.BookRecommendRepository;
import com.mb.repository.BookRequestRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookRequestService {
    private final BookRequestRepository bookRequestRepository;

    public void save(BookRequest bookRequest) {
        bookRequestRepository.save(bookRequest);
    }

    public BookRequest findById(Long bookRequestId) {
        return bookRequestRepository.findById(bookRequestId).orElseThrow(()-> new IllegalArgumentException("해당 요청이 없습니다."));
    }

    public List<BookRequest> findBookRequestListByMemberAndKeyword(Member loginMember, String searchText, Pageable pageable) {
        QBookRequest qBookRequest = QBookRequest.bookRequest;

        BooleanExpression predicate = qBookRequest.member.eq(loginMember)
                .and(qBookRequest.bookName.containsIgnoreCase(searchText));
        List<BookRequest> bookRequestList = bookRequestRepository.findAll(predicate, pageable).getContent();
        return bookRequestList;
    }

    public Integer getBookRequestListByMemberAndKeywordCnt(Member loginMember, String searchText) {
        QBookRequest qBookRequest = QBookRequest.bookRequest;

        BooleanExpression predicate = qBookRequest.member.eq(loginMember)
                .and(qBookRequest.bookName.containsIgnoreCase(searchText));
        List<BookRequest> bookRequestList = (List<BookRequest>) bookRequestRepository.findAll(predicate);
        return bookRequestList.size();
    }

    public List<BookRequest> findAllBookRequestListAndKeyword(String searchText, Pageable pageable) {
        QBookRequest qBookRequest = QBookRequest.bookRequest;

        BooleanExpression predicate = qBookRequest.bookName.containsIgnoreCase(searchText);
        List<BookRequest> bookRequestList = bookRequestRepository.findAll(predicate, pageable).getContent();
        return bookRequestList;
    }

    public Integer getAllBookRequestListAndKeywordCnt(String searchText) {
        QBookRequest qBookRequest = QBookRequest.bookRequest;

        BooleanExpression predicate = qBookRequest.bookName.containsIgnoreCase(searchText);
        List<BookRequest> bookRequestList = (List<BookRequest>) bookRequestRepository.findAll(predicate);
        return bookRequestList.size();
    }
}
