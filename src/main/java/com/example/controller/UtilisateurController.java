package com.example.controller;

import com.example.configuration.JwtTokenUtil;
import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "http://localhost:3000")

public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/hello")
    public String helloworld() {

        return "helloworld";
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Utilisateur user) {
        try {
            user.setType(TypeUtilisateur.CLIENT);

            utilisateurService.registerUser(user);
            return ResponseEntity.ok("Inscription réussie");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/UpdateProfile")
    public ResponseEntity<String> modifierProfil(HttpServletRequest request, @RequestBody Utilisateur utilisateurModifie) {
       Utilisateur utilisateur = utilisateurService.getUtilisateurFromToken(request);

        utilisateur.setNom(utilisateurModifie.getNom());
        utilisateur.setPrenom(utilisateurModifie.getPrenom());
        utilisateur.setAdresse(utilisateurModifie.getAdresse());
        utilisateur.setNumeroDeTelephone(utilisateurModifie.getNumeroDeTelephone());

        utilisateurService.enregistrerUtilisateur(utilisateur);

        return ResponseEntity.ok("Profil utilisateur mis à jour avec succès");
    }

    @PutMapping("/changer-mot-de-passe")
    public ResponseEntity<String> changerMotDePasse(HttpServletRequest request, @RequestParam String ancienMotDePasse, @RequestParam String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromToken(request);

        if (!utilisateur.getMotDePasse().equals(ancienMotDePasse)) {
            return ResponseEntity.badRequest().body("Le mot de passe actuel est incorrect");
        }

        utilisateur.setMotDePasse(nouveauMotDePasse);
        utilisateurService.enregistrerUtilisateur(utilisateur);

        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }
}


