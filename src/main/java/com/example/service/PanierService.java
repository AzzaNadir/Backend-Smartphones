package com.example.service;

import com.example.model.LignePanier;
import com.example.model.Panier;
import com.example.model.Produit;
import com.example.model.Utilisateur;
import com.example.repository.PanierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PanierService {
    @Autowired
    private PanierRepository panierRepository;

    public void ajouterProduitAuPanier(Utilisateur utilisateur, Produit produit, int quantite) {
        Panier panier = utilisateur.getPanier();
        if (panier == null) {
            panier = new Panier();
            panier.setUtilisateur(utilisateur);

        }

        LignePanier lignePanier = new LignePanier();
        lignePanier.setProduit(produit);
        lignePanier.setQuantite(quantite);

        panier.ajouterLignePanier(lignePanier);

        panierRepository.save(panier);
    }
}