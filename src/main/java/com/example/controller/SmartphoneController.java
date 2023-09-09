package com.example.controller;

import com.example.model.*;
import com.example.service.ProduitService;
import com.example.service.SmartphoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/api")
public class SmartphoneController {
    @Autowired
    private ProduitService produitService;
    @Autowired
    private SmartphoneService smartphoneService;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
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

        List<Produit> existingSmartphones = smartphoneService.rechercherSmartphonesParCritere(marque, modele, couleur, stockage);
        if (!existingSmartphones.isEmpty()) {
            // Un smartphone avec les mêmes attributs existe déjà, renvoyer une réponse d'erreur appropriée
            return new ResponseEntity<>(HttpStatus.CONFLICT); // HTTP 409 - Conflict
        }

        Smartphone smartphone = new Smartphone(nom, description, prix, quantiteStock, image, modele, MarqueSmartphone.valueOf(marque.toUpperCase()), Couleur.valueOf(couleur.toUpperCase()), stockage, memoireRam, tailleEcran);
        smartphoneService.ajouterSmartphone(smartphone);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
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
    public ResponseEntity<Page<Produit>> getSmartphonePresentation(Pageable pageable) {
        Page<Produit> smartphones = smartphoneService.SmartphonePresentation(pageable);
        return ResponseEntity.ok(smartphones);
    }

    @GetMapping("/Smartphones/{id}")
    public ResponseEntity<Produit> getSmartphoneById(@PathVariable Long id) {
        Produit produit = produitService.trouverProduitParId(id);
        if (produit != null) {
            return new ResponseEntity<>(produit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/Smartphones/{id}/availableOptions")
    public ResponseEntity<AvailableOptions> getAvailableOptionsForSmartphone(@PathVariable Long id) {
        Produit produit = produitService.trouverProduitParId(id);
        if (produit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (produit instanceof Smartphone) {
            Smartphone smartphone = (Smartphone) produit;
            MarqueSmartphone marque = smartphone.getMarque();
            String modele = smartphone.getModele();

            List<Produit> availableColors = smartphoneService.getAvailableColorsByMarqueAndModele(marque, modele);
            List<Produit> availableStorages = smartphoneService.getAvailableStoragesByMarqueAndModele(marque, modele);

            AvailableOptions options = new AvailableOptions(availableColors, availableStorages);
            return new ResponseEntity<>(options, HttpStatus.OK);
        } else {
            // Le produit trouvé n'est pas un smartphone
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/Smartphones")
    public ResponseEntity<Page<Produit>> getSmartphonesByFiltersAndPagination(
            @RequestParam(value = "marques", required = false) List<MarqueSmartphone> marques,
            @RequestParam(value = "modeles", required = false) List<String> modeles,
            @RequestParam(value = "couleurs", required = false) List<Couleur> couleurs,
            @RequestParam(value = "stockages", required = false) List<String> stockages,
            @RequestParam(value = "memoireRams", required = false) List<String> memoireRams,
            @RequestParam(value = "tailleEcrans", required = false) List<Double> tailleEcrans,
            Pageable pageable) {

        System.out.println("mes marques" + marques);
        System.out.println("mes couleurs" + couleurs
        );

        Page<Produit> smartphones = smartphoneService.findSmartphonesParCritere(marques, modeles, couleurs, tailleEcrans, memoireRams, stockages, pageable);
        return ResponseEntity.ok(smartphones);
    }

    @GetMapping("/GetSmartphonesByCaracteristiques")
    public ResponseEntity<List<Produit>> searchSmartphonesByOptions(@RequestParam(value = "marque") MarqueSmartphone marque,
                                                                    @RequestParam(value = "modele") String modele,
                                                                    @RequestParam(value = "couleur", required = false) Couleur couleur,
                                                                    @RequestParam(value = "stockage", required = false) String stockage) {

        List<Produit> smartphones = smartphoneService.searchSmartphonesByOptions(marque, modele, couleur, stockage);
        if (smartphones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(smartphones, HttpStatus.OK);
    }


}






