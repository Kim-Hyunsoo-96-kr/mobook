package com.mb.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class WebHook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long webHookId;

    private String email;

    private boolean isAdmin;

    private String hookUrl;

    private String webHookKey;

    private String webHookToken;

}
