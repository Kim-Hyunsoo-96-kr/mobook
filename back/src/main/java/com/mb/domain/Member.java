package com.mb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    @GeneratedValue
    private Long memberId;

    private String id;

    private String password;

    private String email;

    private Boolean isAdmin;

}
