package com.example.controller;

import com.example.configuration.JwtTokenUtil;
import com.example.model.Commande;
import com.example.model.Utilisateur;
import com.example.repository.CommandeRepository;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
public class CommandeControlleur {
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CommandeRepository commandeRepository;
    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/commande")
    public ResponseEntity<List<Commande>>afficherCommandeUtilisateur(HttpServletRequest request) {
        // Récupérer l'adresse e-mail de l'utilisateur à partir du token
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromToken(request);

        List<Commande> commande = utilisateur.getCommandes();

        return ResponseEntity.ok(commande);
    }


    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    @GetMapping("/commande/all")
    public ResponseEntity<List<Commande>> afficherToutesLesCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        return ResponseEntity.ok(commandes);
    }
}
