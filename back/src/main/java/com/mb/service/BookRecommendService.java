package com.mb.service;


import com.mb.domain.Book;
import com.mb.domain.BookRecommend;
import com.mb.domain.Member;
import com.mb.repository.BookRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookRecommendService {

    private final BookRecommendRepository bookRecommendRepository;
    public void save(BookRecommend bookRecommend) {
        bookRecommendRepository.save(bookRecommend);
    }

    public Optional<BookRecommend> findByMemberAndBook(Book book, Member member) {
        return bookRecommendRepository.findByMemberAndBook(member, book);
    }

    public void delete(Optional<BookRecommend> bookHeart) {
        bookRecommendRepository.delete(bookHeart.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 데이터입니다.")));
    }

    public List<BookRecommend> findByMember(Member member) {
        return bookRecommendRepository.findByMember(member);
    }
}
