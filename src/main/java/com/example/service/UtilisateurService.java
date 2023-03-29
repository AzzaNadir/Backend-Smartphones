package com.example.service;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur creerAdmin(String nom, String prenom, String email, String motDePasse, String adresse) {
        Utilisateur admin = utilisateurRepository.findByType(TypeUtilisateur.ADMINISTRATEUR);
        if (admin == null) {
            admin = new Utilisateur();
            admin.setNom(nom);
            admin.setPrenom(prenom);
            admin.setEmail(email);
            String motDePasseEncode = passwordEncoder.encode(motDePasse);
            admin.setMotDePasse(motDePasseEncode);
            admin.setAdresse(adresse);
            admin.setType(TypeUtilisateur.ADMINISTRATEUR);
            utilisateurRepository.save(admin);
        }
        return admin;
    }

}





