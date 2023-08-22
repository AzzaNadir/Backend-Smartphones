package com.example.controller;

import com.example.configuration.JwtTokenUtil;
import com.example.model.LignePanier;
import com.example.model.Panier;
import com.example.model.Produit;
import com.example.model.Utilisateur;
import com.example.service.PanierService;
import com.example.service.ProduitService;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")

public class PanierController {
    @Autowired
    private PanierService panierService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProduitService produitService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/ajouter-au-panier")
    public ResponseEntity<String> ajouterProduitAuPanier(HttpServletRequest request,
                                                         @RequestParam Long produitId,
                                                         @RequestParam int quantite) {
        // Récupérer l'adresse e-mail de l'utilisateur à partir du token
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        // Ensuite, utilisez le service ou le référentiel pour trouver l'utilisateur par adresse e-mail
        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas !");
        }

        Produit produit = produitService.trouverProduitParId(produitId);
        // Vérifier si l'utilisateur et le produit existent
        if (produit == null) {
            return ResponseEntity.badRequest().body("Le produit n'existe pas !");
        }

        // Appeler le service pour ajouter le produit au panier de l'utilisateur
        try {
            panierService.ajouterProduitAuPanier(utilisateur, produit, quantite);
            return ResponseEntity.ok("Produit ajouté au panier avec succès !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Le produit est déjà dans le panier !");
        }
    }
    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/panier")
    public ResponseEntity<Panier> afficherPanierUtilisateur(HttpServletRequest request) {
        // Récupérer l'adresse e-mail de l'utilisateur à partir du token
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        // Ensuite, utilisez le service ou le référentiel pour trouver l'utilisateur par adresse e-mail
        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
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
    @PreAuthorize("hasAuthority('CLIENT')")
    @DeleteMapping("/panier/article/{ligneId}")
    public ResponseEntity<String> supprimerArticleDuPanier(@PathVariable Long ligneId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Panier panier = utilisateur.getPanier();
        if (panier == null) {
            return ResponseEntity.notFound().build();
        }

        List<LignePanier> lignesPanier = panier.getLignesPanier();

        LignePanier ligneASupprimer = lignesPanier.stream()
                .filter(ligne -> ligne.getId().equals(ligneId))
                .findFirst()
                .orElse(null);

        if (ligneASupprimer == null) {
            return ResponseEntity.notFound().build();
        }

        lignesPanier.remove(ligneASupprimer);
        // Mettez à jour la quantité et les prix totaux du panier ici
        panierService.miseAJourPrixTotalPanier(panier);

        panierService.enregistrerPanier(panier); // Sauvegarder les modifications


        return ResponseEntity.ok("Article supprimé du panier avec succès");
    }
    @PreAuthorize("hasAuthority('CLIENT')")
    @DeleteMapping("/panier/vider")
    public ResponseEntity<String> viderPanier(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Panier panier = utilisateur.getPanier();
        if (panier == null) {
            return ResponseEntity.notFound().build();
        }

        panier.getLignesPanier().clear();
        panierService.miseAJourPrixTotalPanier(panier);
        panierService.enregistrerPanier(panier);

        return ResponseEntity.ok("Panier vidé avec succès");
    }
    @PreAuthorize("hasAuthority('CLIENT')")
    @PutMapping("/article/{ligneId}")
    public ResponseEntity<String> modifierQuantiteLigne(HttpServletRequest request, @PathVariable Long ligneId, @RequestParam("nouvelleQuantite") int nouvelleQuantite) {
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Panier panier = utilisateur.getPanier();
        if (panier == null) {
            return ResponseEntity.notFound().build();
        }

        List<LignePanier> lignesPanier = panier.getLignesPanier();

        LignePanier lignePanier = lignesPanier.stream()
                .filter(ligne -> ligne.getId().equals(ligneId))
                .findFirst()
                .orElse(null);

        if (lignePanier == null) {
            return ResponseEntity.notFound().build();
        }

        Produit produit = lignePanier.getProduit();
        int quantiteEnStock = produit.getQuantiteStock(); // Remplacez ceci par la façon d'obtenir la quantité en stock du produit

        if (nouvelleQuantite > quantiteEnStock) {
            return ResponseEntity.badRequest().body("Quantité en stock insuffisante");
        }

        if (nouvelleQuantite <= 0) {
            // Supprimez la ligne de panier si la quantité est mise à zéro
            panier.getLignesPanier().remove(lignePanier);
        } else {
            lignePanier.setQuantite(nouvelleQuantite);
        }
        double prixUnitaire = produit.getPrix();
        int quantite = lignePanier.getQuantite();
        double prixTotal = prixUnitaire * quantite;
        lignePanier.setPrixTotal(prixTotal);

        panierService.miseAJourPrixTotalPanier(panier);
        panierService.enregistrerPanier(panier);

        return ResponseEntity.ok("Quantité de la ligne de panier modifiée avec succès");

    }

}