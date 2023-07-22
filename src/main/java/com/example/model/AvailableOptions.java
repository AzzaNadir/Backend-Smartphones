package com.example.model;

import java.util.List;

public class AvailableOptions {
    private List<Produit> couleur;
    private List<Produit> stockage;

    public AvailableOptions(List<Produit> couleur, List<Produit> stockage) {
        this.couleur = couleur;
        this.stockage = stockage;
    }

    public List<Produit> getCouleur() {
        return couleur;
    }

    public void setCouleur(List<Produit> couleur) {
        this.couleur = couleur;
    }

    public List<Produit> getStockage() {
        return stockage;
    }

    public void setStockage(List<Produit> stockage) {
        this.stockage = stockage;
    }
}
