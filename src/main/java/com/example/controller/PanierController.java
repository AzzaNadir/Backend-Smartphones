package com.example.controller;

import com.example.model.Panier;
import com.example.model.Produit;
import com.example.model.Utilisateur;
import com.example.service.PanierService;
import com.example.service.ProduitService;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        try {
            panierService.ajouterProduitAuPanier(utilisateur, produit, quantite);
            return ResponseEntity.ok("Produit ajouté au panier avec succès !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Le produit est déjà dans le panier !");
        }
    }

    @GetMapping("/panier")
    public ResponseEntity<Panier> afficherPanierUtilisateur(@RequestParam Long utilisateurId) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Panier panier = utilisateur.getPanier();
        if (panier == null) {
            // Si l'utilisateur n'a pas encore de panier, vous pouvez retourner un panier vide ou une réponse appropriée
            // Ici, nous allons retourner un panier vide
            panier = new Panier();
        }

        return ResponseEntity.ok(panier);
    }

}