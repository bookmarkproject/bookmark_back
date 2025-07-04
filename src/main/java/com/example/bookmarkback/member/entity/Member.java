package com.example.bookmarkback.member.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter
public class Member extends BaseEntity {
    public Member(String email, String password, String name, String nickname, String gender,
                  String phoneNumber,
                  LocalDate birthday, String profileImage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.gender = Gender.toEnum(gender);
        this.role = Role.NORMAL;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.profileImage = profileImage;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            initialValue = 1,
            allocationSize = 50
    )
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(length = 16, nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(name = "profile_image")
    private String profileImage;
}
