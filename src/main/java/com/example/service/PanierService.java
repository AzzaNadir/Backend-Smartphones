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

        LignePanier nouvelleLigne = new LignePanier();
        nouvelleLigne.setProduit(produit);
        nouvelleLigne.setQuantite(quantite);

        // Calculer le prix unitaire et le prix total pour cette ligne
        double prixUnitaire = produit.getPrix(); // Vous pouvez obtenir le prix du produit à partir de son entité
        double prixTotal = prixUnitaire * quantite;
        nouvelleLigne.setPrixUnitaire(prixUnitaire);
        nouvelleLigne.setPrixTotal(prixTotal);

        panier.ajouterLignePanier(nouvelleLigne);
        miseAJourPrixTotalPanier(panier);

        // Enregistrez le panier uniquement, pas besoin de sauvegarder l'utilisateur ici
        panierRepository.save(panier);
    }

    private void miseAJourPrixTotalPanier(Panier panier) {
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
}
