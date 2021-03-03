package com.interview.usermanagementsystem.repository;

import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByStatusNotOrderByRegisteredAtAsc(Status status, Pageable pageable);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByCode(String code);
}
