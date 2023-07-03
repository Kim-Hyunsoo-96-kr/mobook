package com.mb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Member {
    @Id
    @GeneratedValue
    private Long memberId;

    private String id;

    private String password;

    private String email;

    private Boolean isAdmin;

}
