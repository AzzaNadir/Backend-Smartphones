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
            utilisateur.setPanier(panier);
        }

        // Vérifier si le produit avec le même ID existe déjà dans le panier
        for (LignePanier lignePanier : panier.getLignesPanier()) {
            if (lignePanier.getProduit().getId().equals(produit.getId())) {
                // Le produit existe déjà dans le panier, lancez une exception
                throw new IllegalArgumentException("Le produit est déjà dans le panier !");
            }
        }

        // Le produit n'existe pas dans le panier, ajoutez-le en tant que nouvelle ligne
        LignePanier nouvelleLigne = new LignePanier();
        nouvelleLigne.setProduit(produit);
        nouvelleLigne.setQuantite(quantite);
        panier.ajouterLignePanier(nouvelleLigne);

        // Enregistrez le panier uniquement, pas besoin de sauvegarder l'utilisateur ici
        panierRepository.save(panier);
    }
}