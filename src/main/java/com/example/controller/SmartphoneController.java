package com.example.controller;

import com.example.model.Couleur;
import com.example.model.MarqueSmartphone;
import com.example.model.Smartphone;
import com.example.service.SmartphoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class SmartphoneController {

    @Autowired
    private SmartphoneService smartphoneService;

    @PostMapping("/smartphones")
    public ResponseEntity<Smartphone> addSmartphone(@RequestParam("nom") String nom,
                                                    @RequestParam("description") String description,
                                                    @RequestParam("prix") float prix,
                                                    @RequestParam("quantiteStock") int quantiteStock,
                                                    @RequestParam("image") MultipartFile file,
                                                    @RequestParam("modele") String modele,
                                                    @RequestParam("marque") String marque,
                                                    @RequestParam("couleur") String couleur,
                                                    @RequestParam("stockage") String stockage,
                                                    @RequestParam("memoireRam") String memoireRam,
                                                    @RequestParam("tailleEcran") double tailleEcran) throws IOException {
        byte[] image = file.getBytes();
        Smartphone smartphone = new Smartphone(nom, description, prix, quantiteStock, image, modele, MarqueSmartphone.valueOf(marque.toUpperCase()), Couleur.valueOf(couleur.toUpperCase()), stockage, memoireRam, tailleEcran);
        smartphoneService.ajouterSmartphone(smartphone);
        return new ResponseEntity<>(HttpStatus.CREATED);
        //        return new ResponseEntity<>(addedSmartphone, HttpStatus.CREATED);

    }

}






