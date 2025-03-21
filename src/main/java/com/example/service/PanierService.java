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

        for (LignePanier lignePanier : panier.getLignesPanier()) {
            if (lignePanier.getProduit().getId().equals(produit.getId())) {
                throw new IllegalArgumentException("Le produit est déjà dans le panier !");
            }
        }

        LignePanier nouvelleLigne = new LignePanier();
        nouvelleLigne.setProduit(produit);
        nouvelleLigne.setQuantite(quantite);

        double prixUnitaire = produit.getPrix();
        double prixTotal = prixUnitaire * quantite;
        nouvelleLigne.setPrixUnitaire(prixUnitaire);
        nouvelleLigne.setPrixTotal(prixTotal);

        panier.ajouterLignePanier(nouvelleLigne);
        miseAJourPrixTotalPanier(panier);

        panierRepository.save(panier);
    }

    public void miseAJourPrixTotalPanier(Panier panier) {
        double prixTotalPanier = 0.0;
        for (LignePanier lignePanier : panier.getLignesPanier()) {
            prixTotalPanier += lignePanier.getPrixTotal();
        }
        panier.setPrixTotalPanier(prixTotalPanier);
    }

    public Panier findByUtilisateurId(Long utilisateurId) {
        return panierRepository.findByUtilisateurId(utilisateurId);
    }

    public void clearPanier(Panier panier) {
        panier.getLignesPanier().clear();
        panier.setPrixTotalPanier(0.0);
        panierRepository.save(panier);
    }

    public void enregistrerPanier(Panier panier) {
        panierRepository.save(panier);
    }
}
