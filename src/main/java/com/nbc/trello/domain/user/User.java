package com.nbc.trello.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    @Column(nullable = true)
    private Long kakaoId;

    public User(String email, String password, String nickname, UserRoleEnum userRole) {
        this.email = email;
        this.password = password;
        this.username = nickname;
        this.userRole = userRole;
    }

    public User(String email, String password, String nickname, UserRoleEnum userRole,
        Long kakaoId) {
        this.email = email;
        this.password = password;
        this.username = nickname;
        this.userRole = userRole;
        this.kakaoId = kakaoId;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void updatedUsername(String username) {
        this.username = username;
    }
}
