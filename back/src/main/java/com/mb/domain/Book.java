package com.mb.domain;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String bookName;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member rentalMember;
}
