package com.example.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande")
@Data
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    @Column(name = "total_commande")
    private BigDecimal totalCommande;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    // Ajoutez d'autres attributs et méthodes selon vos besoins
}
