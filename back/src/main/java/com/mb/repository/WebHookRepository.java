package com.mb.repository;

import com.mb.domain.WebHook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebHookRepository extends JpaRepository<WebHook, Long> {
    Optional<WebHook> findByEmail(String email);

    Optional<WebHook> findByIsAdmin(Boolean isAdmin);
}
