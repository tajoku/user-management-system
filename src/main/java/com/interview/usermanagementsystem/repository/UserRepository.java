package com.interview.usermanagementsystem.repository;

import com.interview.usermanagementsystem.enums.Status;
import com.interview.usermanagementsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByStatusNotOrderByRegisteredAtAsc(Status status, Pageable pageable);
}
