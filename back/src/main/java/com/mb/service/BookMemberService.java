package com.mb.service;


import com.mb.domain.BookHistory;
import com.mb.domain.Member;
import com.mb.repository.BookMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookMemberService {
    private final BookMemberRepository bookMemberRepository;
    public void addBookMember(BookHistory bookHistory) {
        bookMemberRepository.save(bookHistory);
    }

    public List<BookHistory> findBookLogByMemberId(Member member) {
        List<BookHistory> list =  bookMemberRepository.findByMember(member);
        return list;
    }
}
