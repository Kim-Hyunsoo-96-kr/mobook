package com.mb.domain;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long bookId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "MEMBER_UUID")
    private Member rentalMember;
}
