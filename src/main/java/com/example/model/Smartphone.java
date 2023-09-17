package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("SMARTPHONE")
@Table(name = "smartphone")
public class Smartphone extends Produit {
    @NotBlank
    @Column(name = "modele")
    private String modele;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "marque")
    private MarqueSmartphone marque;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "couleur")
    private Couleur couleur;
    @NotBlank
    @Column(name = "stockage")
    private String stockage;
    @NotBlank
    @Column(name = "memoire_ram")
    private String memoireRam;
    @NotNull
    @Column(name = "taille_ecran")
    private double tailleEcran;

    public Smartphone() {
        super();

    }

    public Smartphone(String nom, String description, double prix, int quantiteStock, byte[] image, String modele, MarqueSmartphone marque, Couleur couleur, String stockage, String memoireRam, double tailleEcran) {
        super(nom, description, prix, quantiteStock, image);
        this.modele = modele;
        this.marque = marque;
        this.couleur = couleur;
        this.stockage = stockage;
        this.memoireRam = memoireRam;
        this.tailleEcran = tailleEcran;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public MarqueSmartphone getMarque() {
        return marque;
    }

    public void setMarque(MarqueSmartphone marque) {
        this.marque = marque;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    public String getStockage() {
        return stockage;
    }

    public void setStockage(String stockage) {
        this.stockage = stockage;
    }

    public String getMemoireRam() {
        return memoireRam;
    }

    public void setMemoireRam(String memoireRam) {
        this.memoireRam = memoireRam;
    }

    public double getTailleEcran() {
        return tailleEcran;
    }

    public void setTailleEcran(double tailleEcran) {
        this.tailleEcran = tailleEcran;
    }
}
