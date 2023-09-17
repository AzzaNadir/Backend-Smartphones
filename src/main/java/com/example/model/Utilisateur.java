package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Component
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nom_utilisateur", nullable = false)
    private String nom;

    @NotBlank
    @Size(max = 50)
    @Column(name = "prenom_utilisateur", nullable = false)
    private String prenom;

    @NotBlank
    @Email
    @Size(max = 50)
    @Column(name = "email_utilisateur", nullable = false)
    private String email;

    @NotBlank
    @Column(name = "mot_de_passe_utilisateur", nullable = false)
    private String motDePasse;
    @NotBlank
    @Column(name = "numero_telephone_utilisateur", nullable = false)
    private String numeroDeTelephone;
    @NotBlank
    @Size(max = 100)
    @Column(name = "adresse_utilisateur", nullable = false)
    private String adresse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_utilisateur", nullable = false)
    private TypeUtilisateur type;

    @OneToOne(mappedBy = "utilisateur")
    @JsonIgnore
    private Panier panier;
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Commande> commandes = new ArrayList<>();

    public Utilisateur() {
        super();

    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String adresse, TypeUtilisateur type, String numeroDeTelephone) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.type = type;
        this.numeroDeTelephone = numeroDeTelephone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public TypeUtilisateur getType() {
        return type;
    }

    public void setType(TypeUtilisateur type) {
        this.type = type;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

    public String getNumeroDeTelephone() {
        return numeroDeTelephone;
    }

    public void setNumeroDeTelephone(String numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
    }
}
