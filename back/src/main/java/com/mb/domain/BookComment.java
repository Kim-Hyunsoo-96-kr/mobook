package com.mb.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private String regDate;

    private String memberName;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


}
