package com.example.repository;

import com.example.model.Couleur;
import com.example.model.MarqueSmartphone;
import com.example.model.Produit;
import com.example.model.Smartphone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone")
    public List<Produit> SmartphonePresentation();


    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone AND p.marque = IFNULL(:marque, p.marque) AND p.modele = IFNULL(:modele, p.modele) AND p.couleur = IFNULL(:couleur, p.couleur) AND p.tailleEcran = IFNULL(:tailleEcran, p.tailleEcran) AND p.memoireRam = IFNULL(:memoireRam, p.memoireRam) AND p.stockage = IFNULL(:stockage, p.stockage)\n")
    public List<Produit> findSmartphonesByCriteria(@Param("marque") String marque, @Param("modele") String modele, @Param("couleur") String couleur, @Param("tailleEcran") Double tailleEcran, @Param("memoireRam") String memoireRam, @Param("stockage") String stockage);

    List<Smartphone> findSmartphonesByStockageAndCouleur(Integer stockage, String couleur);


    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone AND  p.marque = :marque AND p.modele = :modele")
    List<Produit> findAvailableStoragesByMarqueAndModele(@Param("marque") MarqueSmartphone marque, @Param("modele") String modele);

    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone AND  p.marque = :marque AND p.modele = :modele")
    List<Produit> findAvailableColorsByMarqueAndModele(@Param("marque") MarqueSmartphone marque, @Param("modele") String modele);

    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone AND p.marque = :marque AND p.modele = :modele AND (p.couleur = :couleur AND p.stockage = :stockage)")
    List<Produit> findSmartphonesByCaracteristiques(@Param("marque") MarqueSmartphone marque,
                                                    @Param("modele") String modele,
                                                    @Param("couleur") Couleur couleur,
                                                    @Param("stockage") String stockage);


    @Modifying
    @Query("UPDATE Produit p SET p.quantiteStock = :quantiteStock WHERE p.id = :id")
    void updateQuantiteStockById(Long id, int quantiteStock);
}




