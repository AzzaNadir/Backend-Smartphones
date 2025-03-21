package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnore
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LignePanier> lignesPanier = new ArrayList<>();
    @Column(name = "prixTotalPanier")
    private double prixTotalPanier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<LignePanier> getLignesPanier() {
        return lignesPanier;
    }

    public void setLignesPanier(List<LignePanier> lignesPanier) {
        this.lignesPanier = lignesPanier;
    }

    public void ajouterLignePanier(LignePanier lignePanier) {
        lignesPanier.add(lignePanier);
        lignePanier.setPanier(this);
    }

    public double getPrixTotalPanier() {
        return prixTotalPanier;
    }

    public void setPrixTotalPanier(double prixTotalPanier) {
        this.prixTotalPanier = prixTotalPanier;
    }
}