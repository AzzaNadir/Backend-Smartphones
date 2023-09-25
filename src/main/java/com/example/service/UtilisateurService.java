package com.example.service;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Utilisateur creerAdmin(String nom, String prenom, String email, String motDePasse, String adresse, String numeroDeTelephone) {
        Utilisateur admin = utilisateurRepository.findByType(TypeUtilisateur.ADMINISTRATEUR);
        if (admin == null) {
            admin = new Utilisateur();
            admin.setNom(nom);
            admin.setPrenom(prenom);
            admin.setEmail(email);
            String motDePasseEncode = passwordEncoder.encode(motDePasse);
            admin.setMotDePasse(motDePasseEncode);
            admin.setAdresse(adresse);
            admin.setNumeroDeTelephone(numeroDeTelephone);
            admin.setType(TypeUtilisateur.ADMINISTRATEUR);
            utilisateurRepository.save(admin);
        }
        return admin;
    }

    public void registerUser(Utilisateur user) {
        if (utilisateurRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email déjà utilisé");
        }

        String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());
        user.setMotDePasse(hashedPassword);

        utilisateurRepository.save(user);
    }

    public void enregistrerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public Utilisateur getUtilisateurById(Long id) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isPresent()) {
            return optionalUtilisateur.get();
        } else {
            throw new NoSuchElementException("Utilisateur with ID " + id + " does not exist.");
        }
    }

    public Utilisateur getUtilisateurParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }


    public Utilisateur getUtilisateurFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        Utilisateur utilisateur = getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            throw new RuntimeException("L'utilisateur n'existe pas !");
        }
        return utilisateur;
    }
}



