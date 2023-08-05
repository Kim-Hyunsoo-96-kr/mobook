package com.mb.service;


import com.mb.domain.Book;
import com.mb.domain.BookHeart;
import com.mb.domain.Member;
import com.mb.repository.BookHeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookHeartService {

    private final BookHeartRepository bookHeartRepository;
    public void save(BookHeart bookHeart) {
        bookHeartRepository.save(bookHeart);
    }

    public Optional<BookHeart> findByMemberAndBook(Book book, Member member) {
        return bookHeartRepository.findByMemberAndBook(member, book);
    }

    public void delete(Optional<BookHeart> bookHeart) {
        bookHeartRepository.delete(bookHeart.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 데이터입니다.")));
    }
}
