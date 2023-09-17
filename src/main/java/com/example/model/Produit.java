package com.example.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type_produit")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Smartphone.class, name = "SMARTPHONE")
})

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_produit")
public abstract class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long id;

    @NotBlank
    @Column(name = "nom_produit", nullable = false)
    private String nom;

    @NotBlank
    @Column(name = "description_produit", nullable = false)
    private String description;

    @DecimalMin("0.0")
    @Column(name = "prix_produit", nullable = false)
    private double prix;

    @Min(0)
    @Column(name = "quantite_stock_produit", nullable = false)
    private int quantiteStock;


    @Column(name = "image_produit", unique = false, nullable = false, length = 100000)
    private byte[] image;
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<LignePanier> lignesPanier;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    public Produit() {
    }

    public Produit(String nom, String description, double prix, int quantiteStock, byte[] image) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.image = image;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}