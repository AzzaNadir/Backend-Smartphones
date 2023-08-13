package com.example.repository;

import com.example.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c JOIN FETCH c.utilisateur")
    List<Commande> findAllFetchUtilisateur();
}