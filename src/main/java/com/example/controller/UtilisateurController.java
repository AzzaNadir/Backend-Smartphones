package com.example.controller;

import com.example.model.ChangerMotDePasseRequest;
import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

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

    @GetMapping("/GetUtilisateur")
    public ResponseEntity<Utilisateur> getProfilUtilisateur(HttpServletRequest request) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromToken(request);
        utilisateur.setMotDePasse(null);
        return ResponseEntity.ok(utilisateur);
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
    public ResponseEntity<String> changerMotDePasse(HttpServletRequest request, @RequestBody ChangerMotDePasseRequest requestBody) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurFromToken(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Utilisez l'implémentation de votre choix
        if (!passwordEncoder.matches(requestBody.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            return ResponseEntity.badRequest().body("Le mot de passe actuel est incorrect");
        }

        String nouveauMotDePasseHash = passwordEncoder.encode(requestBody.getNouveauMotDePasse());
        utilisateur.setMotDePasse(nouveauMotDePasseHash);
        utilisateurService.enregistrerUtilisateur(utilisateur);

        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    @GetMapping("/permission")
    public List<String> getPermissions(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }
}


