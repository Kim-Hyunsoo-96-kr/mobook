package com.mb.service;


import com.mb.domain.BookMember;
import com.mb.repository.BookMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMemberService {
    private final BookMemberRepository bookMemberRepository;
    public void addBookMember(BookMember bookMember) {
        bookMemberRepository.save(bookMember);
    }
}
