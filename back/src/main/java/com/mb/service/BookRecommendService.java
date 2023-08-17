package com.mb.service;


import com.mb.domain.*;
import com.mb.repository.BookRecommendRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    public List<BookRecommend> findByMemberAndKeyword(Member loginMember, String searchText, Pageable pageable) {
        QBookRecommend qBookRecommend = QBookRecommend.bookRecommend;

        BooleanExpression predicate = qBookRecommend.member.eq(loginMember)
                .and(qBookRecommend.book.bookName.containsIgnoreCase(searchText));
        List<BookRecommend> bookRecommendList = bookRecommendRepository.findAll(predicate, pageable).getContent();
        return bookRecommendList;
    }

    public Integer getBookRecommendListByMemberAndKeywordCnt(Member loginMember, String searchText) {
        QBookRecommend qBookRecommend = QBookRecommend.bookRecommend;

        BooleanExpression predicate = qBookRecommend.member.eq(loginMember)
                .and(qBookRecommend.book.bookName.containsIgnoreCase(searchText));
        List<BookRecommend> bookRecommendList = (List<BookRecommend>) bookRecommendRepository.findAll(predicate);
        return bookRecommendList.size();
    }
}
