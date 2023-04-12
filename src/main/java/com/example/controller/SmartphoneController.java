package com.example.controller;

import com.example.model.Couleur;
import com.example.model.MarqueSmartphone;
import com.example.model.Produit;
import com.example.model.Smartphone;
import com.example.service.ProduitService;
import com.example.service.SmartphoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class SmartphoneController {
    @Autowired
    private ProduitService produitService;
    @Autowired
    private SmartphoneService smartphoneService;

    @PostMapping("/AddSmartphones")
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
    }

    @PostMapping("/AddSmartphones2")
    public ResponseEntity<Smartphone> addSmartphone(@ModelAttribute Smartphone smartreq, @RequestParam("image") MultipartFile file) throws IOException {

        // Extraction de l'image du formulaire
        byte[] image = file.getBytes();

        Smartphone smartphone = new Smartphone(smartreq.getNom(), smartreq.getDescription(), smartreq.getPrix(), smartreq.getQuantiteStock(), image, smartreq.getModele(), MarqueSmartphone.valueOf(smartreq.getMarque().name().toUpperCase()), Couleur.valueOf(smartreq.getCouleur().name().toUpperCase()), smartreq.getStockage(), smartreq.getMemoireRam(), smartreq.getTailleEcran());

        smartphoneService.ajouterSmartphone(smartphone);
        return new ResponseEntity<>(smartphone, HttpStatus.CREATED);
    }


    //    @PutMapping("/UpdateSmartphones/{id}")
//    public ResponseEntity<Smartphone> updateSmartphone(@PathVariable("id") Long id,
//                                                       @RequestParam("nom") String nom,
//                                                       @RequestParam("description") String description,
//                                                       @RequestParam("prix") float prix,
//                                                       @RequestParam("quantiteStock") int quantiteStock,
//                                                       @RequestParam("image") MultipartFile file,
//                                                       @RequestParam("modele") String modele,
//                                                       @RequestParam("marque") String marque,
//                                                       @RequestParam("couleur") String couleur,
//                                                       @RequestParam("stockage") String stockage,
//                                                       @RequestParam("memoireRam") String memoireRam,
//                                                       @RequestParam("tailleEcran") double tailleEcran) throws IOException {
//
//        byte[] image = file.getBytes();
//        Smartphone smartphone = new Smartphone(nom, description, prix, quantiteStock, image, modele, MarqueSmartphone.valueOf(marque.toUpperCase()), Couleur.valueOf(couleur.toUpperCase()), stockage, memoireRam, tailleEcran);
//        smartphone.setId(id); // set the id of the smartphone
//        smartphoneService.mettreAJourSmartphone(smartphone);
//        return new ResponseEntity<>(smartphone, HttpStatus.OK);
//    }
    @PutMapping("/produits/{id}")
    public ResponseEntity<Smartphone> updateSmartphone(@PathVariable("id") Long id,
                                                       MultipartHttpServletRequest request) throws IOException {
        Produit produit = produitService.trouverProduitParId(id);
        if (produit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Smartphone smartphone;
        if (produit instanceof Smartphone) {
            smartphone = (Smartphone) produit;

            smartphone.setNom(request.getParameter("nom"));
            smartphone.setDescription(request.getParameter("description"));
            smartphone.setPrix(Double.parseDouble(request.getParameter("prix")));
            smartphone.setQuantiteStock(Integer.parseInt(request.getParameter("quantiteStock")));
            smartphone.setModele(request.getParameter("modele"));
            smartphone.setMarque(MarqueSmartphone.valueOf(request.getParameter("marque").toUpperCase()));
            smartphone.setCouleur(Couleur.valueOf(request.getParameter("couleur").toUpperCase()));
            smartphone.setStockage(request.getParameter("stockage"));
            smartphone.setMemoireRam(request.getParameter("memoireRam"));
            smartphone.setTailleEcran(Double.parseDouble(request.getParameter("tailleEcran")));

            // Extraction de l'image du formulaire
            MultipartFile file = request.getFile("image");
            if (file != null && !file.isEmpty()) {
                byte[] bytes = file.getBytes();
                smartphone.setImage(bytes);
            }
        } else {
            throw new IllegalArgumentException("Produit with ID " + id + " is not a Smartphone.");
        }

        smartphoneService.ajouterSmartphone(smartphone);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/AfficherSmartphones")
    public List<Produit> getSmartphonePresentation() {
        return smartphoneService.SmartphonePresentation();
    }

}






