package com.example.service;

import com.example.model.Couleur;
import com.example.model.MarqueSmartphone;
import com.example.model.Produit;
import com.example.model.Smartphone;
import com.example.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@CrossOrigin
public class SmartphoneService {

    @Autowired
    ProduitRepository produitRepository;

    public Smartphone ajouterSmartphone(Smartphone smartphone) {
        return produitRepository.save(smartphone);
    }

    public List<Produit> getAllSmartphones() {
        return produitRepository.findAll();
    }

    public Page<Produit> SmartphonePresentation(Pageable pageable) {
        return produitRepository.findSmartphones(pageable);
    }

    public void mettreAJourSmartphone(Smartphone smartphone) {
        Optional<Produit> optionalProduit = produitRepository.findById(smartphone.getId());
        if (optionalProduit.isPresent()) {
            Produit existingProduit = optionalProduit.get();
            if (existingProduit instanceof Smartphone) {
                Smartphone existingSmartphone = (Smartphone) existingProduit;
                existingSmartphone.setNom(smartphone.getNom());
                existingSmartphone.setDescription(smartphone.getDescription());
                existingSmartphone.setPrix(smartphone.getPrix());
                existingSmartphone.setQuantiteStock(smartphone.getQuantiteStock());
                existingSmartphone.setImage(smartphone.getImage());
                existingSmartphone.setModele(smartphone.getModele());
                existingSmartphone.setMarque(smartphone.getMarque());
                existingSmartphone.setCouleur(smartphone.getCouleur());
                existingSmartphone.setStockage(smartphone.getStockage());
                existingSmartphone.setMemoireRam(smartphone.getMemoireRam());
                existingSmartphone.setTailleEcran(smartphone.getTailleEcran());
                produitRepository.save(existingSmartphone);
            } else {
                throw new IllegalArgumentException("Produit with ID " + smartphone.getId() + " is not a Smartphone.");
            }
        } else {
            throw new NoSuchElementException("Produit with ID " + smartphone.getId() + " does not exist.");
        }
    }

    public List<Produit> rechercherSmartphonesParCritere(String marque, String modele, String couleur,String stockage) {
        return produitRepository.findSmartphonesByCriteria(marque, modele, couleur, stockage);
    }

    public Page<Produit> findSmartphonesParCritere(List<MarqueSmartphone> marques, List<String> modeles, List<Couleur> couleurs, List<Double> tailleEcrans, List<String> memoireRams ,List<String> stockages, Pageable pageable) {

        return produitRepository.findSmartphonesByCritere(
                marques, modeles, couleurs, tailleEcrans, memoireRams, stockages, pageable);
    }


    public List<Produit> getAvailableColorsByMarqueAndModele(MarqueSmartphone marque, String modele) {
        return produitRepository.findAvailableColorsByMarqueAndModele(marque, modele);
    }

    public List<Produit> getAvailableStoragesByMarqueAndModele(MarqueSmartphone marque, String modele) {
        return produitRepository.findAvailableStoragesByMarqueAndModele(marque, modele);
    }

    public List<Produit> searchSmartphonesByOptions(MarqueSmartphone marque, String modele, Couleur couleur, String stockage) {
        return produitRepository.findSmartphonesByCaracteristiques(marque, modele, couleur, stockage);
    }

}
