package com.erp.core_module.repository;

import com.erp.core_module.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
