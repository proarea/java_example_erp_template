package com.erp.core_module.repository;

import com.erp.core_data.enumeration.Role;
import com.erp.core_data.enumeration.UserStatus;
import com.erp.core_module.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByEmailIgnoreCaseOrPhoneIgnoreCase(String email, String phone);

    Optional<UserEntity> findByEmailIgnoreCaseAndStatus(String email, UserStatus status);

    List<UserEntity> findAllByRole(Role role);

    @Query(nativeQuery = true, value = "SELECT * FROM users " +
            "WHERE is_deleted IS FALSE " +
            "AND status = :status " +
            "AND (:search IS NULL " +
            "OR (lower(email) LIKE '%' || lower(:search) || '%' " +
            "OR lower(first_name) LIKE '%' || lower(:search) || '%' " +
            "OR lower(last_name) LIKE '%' || lower(:search) || '%' " +
            "OR lower(phone) LIKE '%' || lower(:search) || '%'))")
    Page<UserEntity> searchUsers(String search, String status, Pageable pageable);

}
