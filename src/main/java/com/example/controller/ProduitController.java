package com.example.controller;

import com.example.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ProduitController {

    @Autowired
    private ProduitService produitService ;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    @DeleteMapping("/DeleteProduits/{id}")
    public ResponseEntity<?> deleteProduit(@PathVariable("id") Long id) {
        try {
            produitService.supprimerProduit(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
