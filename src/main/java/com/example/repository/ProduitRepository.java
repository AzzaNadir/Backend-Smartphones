package com.example.repository;

import com.example.model.Couleur;
import com.example.model.MarqueSmartphone;
import com.example.model.Produit;
import com.example.model.Smartphone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone")
    Page<Produit> findSmartphones(Pageable pageable);


    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone AND p.marque = IFNULL(:marque, p.marque) AND p.modele = IFNULL(:modele, p.modele) AND p.couleur = IFNULL(:couleur, p.couleur) AND p.stockage = IFNULL(:stockage, p.stockage)\n")
    public List<Produit> findSmartphonesByCriteria(@Param("marque") String marque, @Param("modele") String modele, @Param("couleur") String couleur, @Param("stockage") String stockage);

    @Query("SELECT p FROM Produit p WHERE TYPE(p) = Smartphone " +
            "AND ( COALESCE(:marques, null) IS NULL OR p.marque IN :marques ) "+
            "AND (COALESCE(:modeles, null)  IS NULL OR p.modele IN :modeles) " +
            "AND (COALESCE(:couleurs, null)  IS NULL OR p.couleur IN :couleurs) " +
            "AND (COALESCE(:tailleEcrans, null)  IS NULL OR p.tailleEcran IN :tailleEcrans) " +
            "AND (COALESCE(:memoireRams, null)  IS NULL OR p.memoireRam IN :memoireRams) " +
            "AND (COALESCE(:stockages, null)  IS NULL OR p.stockage IN :stockages)")
    Page<Produit> findSmartphonesByCritere(
            @Param("marques") List<MarqueSmartphone> marques,

            List<String> modeles, List<Couleur> couleurs, List<Double> tailleEcrans, List<String> memoireRams, List<String> stockages, Pageable pageable
    );

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



    @Query("SELECT p.quantiteStock FROM Produit p WHERE p.id = :productId")
    int getQuantiteStockById(@Param("productId") Long productId);
}




