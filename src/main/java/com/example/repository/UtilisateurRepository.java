package com.example.repository;

import com.example.model.TypeUtilisateur;
import com.example.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByEmail(String email);

    Utilisateur findByType(TypeUtilisateur administrateur);


}