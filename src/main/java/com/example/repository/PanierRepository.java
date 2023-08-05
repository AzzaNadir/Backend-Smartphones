package com.example.repository;

import com.example.model.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanierRepository extends JpaRepository<Panier, Long> {

    Panier findByUtilisateurId(Long utilisateurId);
}
