package com.example.controller;

import com.example.model.Utilisateur;
import com.example.model.Produit;
import com.example.service.PanierService;
import com.example.service.UtilisateurService;
import com.example.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PanierController {
    @Autowired
    private PanierService panierService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProduitService produitService;

    @PostMapping("/ajouter-au-panier")
    public ResponseEntity<String> ajouterProduitAuPanier(@RequestParam Long utilisateurId,
                                                         @RequestParam Long produitId,
                                                         @RequestParam int quantite) {
        // Récupérer l'utilisateur et le produit à partir de leurs IDs
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId);
        Produit produit = produitService.trouverProduitParId(produitId);

        // Vérifier si l'utilisateur et le produit existent
        if (utilisateur == null || produit == null) {
            return ResponseEntity.badRequest().body("L'utilisateur ou le produit n'existe pas !");
        }

        // Appeler le service pour ajouter le produit au panier de l'utilisateur
        panierService.ajouterProduitAuPanier(utilisateur, produit, quantite);

        return ResponseEntity.ok("Produit ajouté au panier avec succès !");
    }
}
