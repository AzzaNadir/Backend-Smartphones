package com.example.service;

import com.example.model.*;
import com.example.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class CommandeService {
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private Order order;

    //    public void createAndSaveCommande(Panier panier) {
//        Commande commande = new Commande();
//        commande.setUtilisateur(panier.getUtilisateur());
//        commande.setDateCommande(order.getPaymentDate());
//        commande.setTotalCommande(order.getAmount());
//
//        // Créer et associer les lignes de commande à la commande.
//        for (LignePanier lignePanier : panier.getLignesPanier()) {
//            LigneCommande ligneCommande = new LigneCommande();
//            ligneCommande.setCommande(commande);
//            ligneCommande.setProduit(lignePanier.getProduit());
//            ligneCommande.setQuantite(lignePanier.getQuantite());
//            ligneCommande.setPrixUnitaire(lignePanier.getPrixUnitaire());
//            ligneCommande.setTotalLigne(lignePanier.getPrixTotal());
//            commande.getLignesCommande().add(ligneCommande);
//
//        }
//        Utilisateur utilisateur = panier.getUtilisateur();
//        utilisateur.getCommandes().add(commande);
//        commande.setUtilisateur(utilisateur);
//        commandeRepository.save(commande);
//
//    }
    public void createAndSaveCommande(Panier panier) {
        Commande commande = new Commande();
        Utilisateur utilisateur = panier.getUtilisateur();
        commande.setUtilisateur(utilisateur);
        commande.setDateCommande(utilisateur.getOrders().get(0).getPaymentDate());
        commande.setTotalCommande(utilisateur.getOrders().get(0).getAmount());

        // Créer et associer les lignes de commande à la commande.
        for (LignePanier lignePanier : panier.getLignesPanier()) {
            LigneCommande ligneCommande = new LigneCommande();
            ligneCommande.setCommande(commande);
            ligneCommande.setProduit(lignePanier.getProduit());
            ligneCommande.setQuantite(lignePanier.getQuantite());
            ligneCommande.setPrixUnitaire(lignePanier.getPrixUnitaire());
            ligneCommande.setTotalLigne(lignePanier.getPrixTotal());
            commande.getLignesCommande().add(ligneCommande);
        }

        utilisateur.getCommandes().add(commande);
        commandeRepository.save(commande);
    }
}
