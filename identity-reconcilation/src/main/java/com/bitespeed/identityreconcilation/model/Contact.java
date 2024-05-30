package com.bitespeed.identityreconcilation.model;

import com.bitespeed.identityreconcilation.enums.LinkPrecedence;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Contact")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "linkedId", nullable = true)
    private Long linkedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "linkPrecedence")
    private LinkPrecedence linkPrecedence;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
