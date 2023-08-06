package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @OneToOne(targetEntity = Utilisateur.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    public PasswordResetToken() {
        // Constructeur par défaut requis par JPA
    }

    public PasswordResetToken(String token, Utilisateur utilisateur, int expirationTimeInMinutes) {
        this.token = token;
        this.utilisateur = utilisateur;
        this.expiryDateTime = calculateExpiryDateTime(expirationTimeInMinutes);
    }

    private LocalDateTime calculateExpiryDateTime(int expirationTimeInMinutes) {
        return LocalDateTime.now().plusMinutes(expirationTimeInMinutes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }
}
