package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
}
