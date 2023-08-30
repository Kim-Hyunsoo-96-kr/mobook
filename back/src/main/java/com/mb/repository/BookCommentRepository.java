package com.mb.repository;

import com.mb.domain.BookComment;
import com.mb.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
}
