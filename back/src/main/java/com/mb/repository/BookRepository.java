package com.mb.repository;

import com.mb.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    List<Book> findByRentalMemberId(Long memberId);

    Optional<Book> findByBookNumber(String bookNumber);

    List<Book> findTop5ByOrderByBookIdDesc();

    List<Book> findTop5ByOrderByPopularityDesc();
    List<Book> findByBookNameContaining(String keyword);
    List<Book> findByBookNameContaining(String keyword, Pageable pageable);
    List<Book> findByBookNumberContaining(String keyword, Pageable pageable);
    List<Book> findByBookAuthorContaining(String keyword, Pageable pageable);
    List<Book> findByBookPublisherContaining(String keyword, Pageable pageable);
    List<Book> findByBookDescriptionContaining(String keyword, Pageable pageable);
    List<Book> findByBookNumberContaining(String keyword);
    List<Book> findByBookAuthorContaining(String keyword);
    List<Book> findByBookPublisherContaining(String keyword);
    List<Book> findByBookDescriptionContaining(String keyword);
    @Query("SELECT b FROM Book b WHERE " +
            "b.bookName LIKE %:keyword% OR " +
            "b.bookNumber LIKE %:keyword% OR " +
            "b.bookAuthor LIKE %:keyword% OR " +
            "b.bookPublisher LIKE %:keyword% OR " +
            "b.bookDescription LIKE %:keyword%")
    List<Book> findAllContainingKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT b FROM Book b WHERE " +
            "b.bookName LIKE %:keyword% OR " +
            "b.bookNumber LIKE %:keyword% OR " +
            "b.bookAuthor LIKE %:keyword% OR " +
            "b.bookPublisher LIKE %:keyword% OR " +
            "b.bookDescription LIKE %:keyword%")
    List<Book> findAllContainingKeyword(@Param("keyword") String keyword);

}
