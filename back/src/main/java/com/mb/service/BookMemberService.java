package com.mb.service;


import com.mb.domain.BookMember;
import com.mb.domain.Member;
import com.mb.repository.BookMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookMemberService {
    private final BookMemberRepository bookMemberRepository;
    public void addBookMember(BookMember bookMember) {
        bookMemberRepository.save(bookMember);
    }

    public List<BookMember> findBookLogByMemberId(Member member) {
        List<BookMember> list =  bookMemberRepository.findByMember(member);
        return list;
    }
}
