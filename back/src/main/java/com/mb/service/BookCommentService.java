package com.mb.service;

import com.mb.domain.BookComment;
import com.mb.domain.RefreshToken;
import com.mb.repository.BookCommentRepository;
import com.mb.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCommentService {

    private final BookCommentRepository bookCommentRepository;

    public void save(BookComment bookComment) {
        bookCommentRepository.save(bookComment);
    }

    public BookComment findById(Long commentId) {
        return bookCommentRepository.findById(commentId).orElseThrow(()-> new IllegalArgumentException("찾을 수 없는 댓글입니다."));
    }

    public void delete(BookComment bookComment) {
        bookCommentRepository.delete(bookComment);
    }
}
