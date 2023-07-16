package com.example.repository;

import com.example.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
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






//    @Query("SELECT p FROM Produit p WHERE TYPE(p) = com.example.model.Smartphone " +
//            "AND (:marques IS NULL OR p.marque IN :marques) " +
//            "AND (:modele IS NULL OR p.modele = :modele) " +
//            "AND (:couleur IS NULL OR p.couleur = :couleur) " +
//            "AND (:tailleEcran IS NULL OR p.tailleEcran = :tailleEcran) " +
//            "AND (:memoireRam IS NULL OR p.memoireRam = :memoireRam) " +
//            "AND (:stockage IS NULL OR p.stockage = :stockage)")
//    public List<Produit> findSmartphonesByCriteria(
//            @Param("marques") List<MarqueSmartphone> marques,
//            @Param("modele") String modele,
//            @Param("couleur") Couleur couleur,
//            @Param("tailleEcran") Double tailleEcran,
//            @Param("memoireRam") String memoireRam,
//            @Param("stockage") String stockage);
}



