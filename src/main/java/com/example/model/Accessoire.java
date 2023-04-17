package com.example.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ACCESSOIRE")
public class Accessoire extends Produit {
    @Column(name = "categorie")
    private String categorie;

//    @Enumerated(EnumType.STRING)
//    private MarqueAccessoire marque;
//
//    public Accessoire() {
//        super();
//    }
//
//    public Accessoire(String categorie, MarqueAccessoire marque) {
//        super();
//        this.categorie = categorie;
//        this.marque = marque;
//    }
//
//    public String getCategorie() {
//        return categorie;
//    }
//
//    public void setCategorie(String categorie) {
//        this.categorie = categorie;
//    }
//
//    public MarqueAccessoire getMarque() {
//        return marque;
//    }
//
//    public void setMarque(MarqueAccessoire marque) {
//        this.marque = marque;
//    }
}