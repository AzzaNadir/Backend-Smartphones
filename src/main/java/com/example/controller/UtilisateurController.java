package com.example.controller;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "http://localhost:3000")

public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;
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
}


