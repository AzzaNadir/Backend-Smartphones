package com.example.service;

import com.example.model.*;
import com.example.repository.CommandeRepository;
import com.example.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class CommandeService {
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private Order order;
    @Autowired
    ProduitRepository produitRepository;

    @Transactional
    public void createAndSaveCommande(Panier panier, List<LignePanier> lignesPanier) {
        Commande commande = new Commande();
        Utilisateur utilisateur = panier.getUtilisateur();
        commande.setUtilisateur(utilisateur);
        commande.setDateTimeCommande(LocalDateTime.now());
        commande.setCommandeStatus("En cours de livraison");
        // Créer et associer les lignes de commande à la commande.
        for (LignePanier lignePanier : panier.getLignesPanier()) {
            LigneCommande ligneCommande = new LigneCommande();
            ligneCommande.setCommande(commande);
            ligneCommande.setProduit(lignePanier.getProduit());
            ligneCommande.setQuantite(lignePanier.getQuantite());
            ligneCommande.setPrixUnitaire(lignePanier.getPrixUnitaire());
            ligneCommande.setTotalLigne(lignePanier.getPrixTotal());
            commande.getLignesCommande().add(ligneCommande);
            // Mise à jour du stock du produit en fonction de la quantité commandée.
            Produit produit = lignePanier.getProduit();
            int nouvelleQuantiteStock = produit.getQuantiteStock() - lignePanier.getQuantite();

            if (nouvelleQuantiteStock >= 0) {
                produit.setQuantiteStock(nouvelleQuantiteStock);
                produitRepository.updateQuantiteStockById(produit.getId(), nouvelleQuantiteStock);
            } else {
                throw new RuntimeException("Stock insuffisant pour le produit : " + produit.getNom());
            }
        }

        utilisateur.getCommandes().add(commande);
        commandeRepository.save(commande);
    }
}