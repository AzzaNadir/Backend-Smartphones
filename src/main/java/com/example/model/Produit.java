package com.example.model;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "produit")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_produit")
public abstract class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long id;

    @NotBlank
    @Column(name = "nom_produit")
    private String nom;

    @NotBlank
    @Column(name = "description_produit")
    private String description;

    @DecimalMin("0.0")
    @Column(name = "prix_produit")
    private double prix;

    @Min(0)
    @Column(name = "quantite_stock_produit")
    private int quantiteStock;

    @NotBlank
    @Column(name = "image_produit")
    private String image;


    public Produit() {
        super();
    }

    public Produit(String nom, String description, double prix, int quantiteStock, String image, TypeProduit typeProduit) {
        super();
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}