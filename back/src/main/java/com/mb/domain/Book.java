package com.mb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
