package com.example.service;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Utilisateur creerAdmin(String nom, String prenom, String email, String motDePasse, String adresse) {
        Utilisateur admin = utilisateurRepository.findByType(TypeUtilisateur.ADMINISTRATEUR);
        if (admin == null) {
            admin = new Utilisateur();
            admin.setNom(nom);
            admin.setPrenom(prenom);
            admin.setEmail(email);
            admin.setMotDePasse(passwordEncoder.encode(motDePasse));
            admin.setAdresse(adresse);
            admin.setType(TypeUtilisateur.ADMINISTRATEUR);
            utilisateurRepository.save(admin);
        }
        return admin;
    }

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        String motDePasse = utilisateur.getMotDePasse();
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur getUtilisateurByEmail(String email) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findByEmail(email);
        if (optionalUtilisateur.isPresent()) {
            return optionalUtilisateur.get();
        } else {
            throw new EntityNotFoundException("Utilisateur with email " + email + " not found");
        }
    }

}





