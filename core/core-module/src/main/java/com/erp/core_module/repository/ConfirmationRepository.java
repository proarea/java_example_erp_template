package com.erp.core_module.repository;

import com.erp.core_module.entity.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

    Optional<ConfirmationEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
