package com.example.service;

import com.example.model.Produit;
import com.example.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    public void supprimerProduit(Long id) {
        produitRepository.deleteById(id);
    }
    public Produit trouverProduitParId(Long id) {
        Optional<Produit> optionalProduit = produitRepository.findById(id);
        if (optionalProduit.isPresent()) {
            return optionalProduit.get();
        } else {
            throw new NoSuchElementException("Produit with ID " + id + " does not exist.");
        }
    }
}