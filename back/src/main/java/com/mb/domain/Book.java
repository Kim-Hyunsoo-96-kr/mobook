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

    private Boolean isAble;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member rentalMember;
}
