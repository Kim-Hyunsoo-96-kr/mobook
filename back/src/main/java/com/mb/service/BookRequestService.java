package com.mb.service;


import com.mb.domain.Book;
import com.mb.domain.BookRecommend;
import com.mb.domain.BookRequest;
import com.mb.domain.Member;
import com.mb.repository.BookRecommendRepository;
import com.mb.repository.BookRequestRepository;
import lombok.RequiredArgsConstructor;
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
}
