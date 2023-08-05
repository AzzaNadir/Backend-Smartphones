package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.ArrayList;
@Entity
@Table(name = "utilisateur")
@Component
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nom_utilisateur")
    private String nom;

    @NotBlank
    @Size(max = 50)
    @Column(name = "prenom_utilisateur")
    private String prenom;

    @NotBlank
    @Email
    @Size(max = 50)
    @Column(name = "email_utilisateur")
    private String email;

    @NotBlank
    @Column(name = "mot_de_passe_utilisateur")
    private String motDePasse;

    @NotBlank
    @Size(max = 100)
    @Column(name = "adresse_utilisateur")
    private String adresse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_utilisateur")
    private TypeUtilisateur type;

    @OneToOne(mappedBy = "utilisateur")
    @JsonIgnore
    private Panier panier;
    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commande> commandes = new ArrayList<>();
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    public Utilisateur() {
        super();

    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String adresse, TypeUtilisateur type) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
        this.type = type;
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
