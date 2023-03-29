package com.example.repository;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    Utilisateur findByType(TypeUtilisateur administrateur);

    Utilisateur findByNom(String username);
}