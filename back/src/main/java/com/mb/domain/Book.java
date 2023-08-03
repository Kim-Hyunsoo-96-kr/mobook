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

    private Integer stars;

    private String regDate;

    private Boolean isAble;

    private Long rentalMemberId;

}
