package com.mb.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;
    private String regDate;
    private String editDate;
    private String title;
    @Column(length = 3000)
    private String contents;
    private Long memberId;
    private String memberName;
}
