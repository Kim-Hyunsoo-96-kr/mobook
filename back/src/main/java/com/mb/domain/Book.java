package com.mb.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String bookName;

    @Column(unique = true)
    private String bookNumber;

    private String bookLink;

    private String bookImageUrl;

    private Integer recommend;

    private String regDate;

    private Boolean isAble;

    private Long rentalMemberId;

    private Boolean isDeleted;

    private String editDate;

    private Integer popularity;

    @JsonManagedReference
    @OneToMany(mappedBy = "book")
    private List<BookComment> bookCommentList;

}
