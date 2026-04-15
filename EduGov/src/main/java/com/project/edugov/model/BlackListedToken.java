package com.project.edugov.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "blacklisted_tokens")
@Data
public class BlackListedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    // We need a large column because JWTs are long strings
    @Column(nullable = false, length = 1000)
    private String token;
}