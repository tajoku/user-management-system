package com.interview.usermanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.usermanagementsystem.enums.Role;
import com.interview.usermanagementsystem.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

@Data
@Builder
@Entity
@Table(name = "user_tbl")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @Enumerated(STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(STRING)
    private Status status;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    @Column(name = "registered_at", updatable = false)
    private Date registeredAt;

    @Column(nullable = false)
    private boolean verified;

    @Temporal(TIMESTAMP)
    @Column(name = "verified_at")
    private Date verifiedAt;

    @Temporal(TIMESTAMP)
    @Column(name = "deactivated_at")
    private Date deactivatedAt;

}
