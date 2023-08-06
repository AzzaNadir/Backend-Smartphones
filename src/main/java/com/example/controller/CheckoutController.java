package com.example.controller;

import com.example.configuration.JwtTokenUtil;
import com.example.model.*;
import com.example.repository.OrderDAO;
import com.example.service.CommandeService;
import com.example.service.PanierService;
import com.example.service.PayPalHttpClient;
import com.example.service.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/checkout")
@Slf4j
public class CheckoutController {

    private final PayPalHttpClient payPalHttpClient;
    private final OrderDAO orderDAO;
    @Autowired
    private PanierService panierService;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private Utilisateur utilisateur;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }


//    @PostMapping
//    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO) throws Exception {
//        var appContext = new PayPalAppContextDTO();
//        appContext.setReturnUrl("http://localhost:8080/checkout/success");
//        appContext.setBrandName("My brand");
//        appContext.setLandingPage(PaymentLandingPage.BILLING);
//        orderDTO.setApplicationContext(appContext);
//        var orderResponse = payPalHttpClient.createOrder(orderDTO);
//
//        List<PurchaseUnit> purchaseUnits = orderDTO.getPurchaseUnits();
//        BigDecimal totalAmount = BigDecimal.ZERO; // Initialiser le montant total à zéro
//
//        for (PurchaseUnit purchaseUnit : purchaseUnits) {
//            MoneyDTO money = purchaseUnit.getAmount();
//            BigDecimal purchaseAmount = new BigDecimal(money.getValue());
//            totalAmount = totalAmount.add(purchaseAmount); // Accumuler le montant total
//        }
//
//        var entity = new Order();
//        entity.setPaypalOrderId(orderResponse.getId());
//        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
//        entity.setAmount(totalAmount);
//        var out = orderDAO.save(entity);
//        log.info("Saved order: {}", out);
//        return ResponseEntity.ok(orderResponse);
//    }

//    @GetMapping(value = "/success")
//    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
//        var orderId = request.getParameter("token");
//
//
//        // Déclencher le processus de capture du paiement.
//        try {
//            payPalHttpClient.captureOrder(orderId);
//            var out = orderDAO.findByPaypalOrderId(orderId);
//            out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
//            out.setPaymentDate(LocalDate.now());
//            orderDAO.save(out);
//
//
////            Panier panier = utilisateur.getPanier();
////
////            if (panier != null) {
////                // Créer la commande à partir du panier et sauvegarder en base de données.
////                commandeService.createAndSaveCommande(panier);
////
////                // Nettoyer le panier après la création de la commande.
////                panierService.clearPanier(panier);
////            } else {
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Panier introuvable");
////            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la capture du paiement");
//        }
//
//        return ResponseEntity.ok().body("Paiement réussi");
//    }


    //    @GetMapping(value = "/success")
//    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
//        var orderId = request.getParameter("token");
//
//        // Déclencher le processus de capture du paiement.
//        try {
//            payPalHttpClient.captureOrder(orderId);
//            var out = orderDAO.findByPaypalOrderId(orderId);
//            out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
//            out.setPaymentDate(LocalDate.now());
//
//            // Récupérer l'adresse e-mail de l'utilisateur à partir du token
//            String token = request.getHeader("Authorization");
//            String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));
//
//            // Ensuite, utilisez le service ou le référentiel pour trouver l'utilisateur par adresse e-mail
//            Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
//            if (utilisateur == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
//            }
//
//            // Associer l'utilisateur à la commande
//            out.setUtilisateur(utilisateur);
//            orderDAO.save(out);
//
//            Panier panier = utilisateur.getPanier();
//
//            if (panier != null) {
//                // Créer la commande à partir du panier et sauvegarder en base de données.
//                commandeService.createAndSaveCommande(panier);
//
//                // Nettoyer le panier après la création de la commande.
//                panierService.clearPanier(panier);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Panier introuvable");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la capture du paiement");
//        }
//
//        return ResponseEntity.ok().body("Paiement réussi");
//    }
    @PostMapping
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO, HttpServletRequest request) throws Exception {
        String token = request.getHeader("Authorization");
        String emailUtilisateur = jwtTokenUtil.getUsernameFromToken(token.substring(7));

        // Ensuite, utilisez le service ou le référentiel pour trouver l'utilisateur par adresse e-mail
        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        // Associer l'utilisateur à la commande
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8080/checkout/success?utilisateur=" + emailUtilisateur);
        appContext.setBrandName("My brand");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        orderDTO.setApplicationContext(appContext);
        var orderResponse = payPalHttpClient.createOrder(orderDTO);

        List<PurchaseUnit> purchaseUnits = orderDTO.getPurchaseUnits();
        BigDecimal totalAmount = BigDecimal.ZERO; // Initialiser le montant total à zéro

        for (PurchaseUnit purchaseUnit : purchaseUnits) {
            MoneyDTO money = purchaseUnit.getAmount();
            BigDecimal purchaseAmount = new BigDecimal(money.getValue());
            totalAmount = totalAmount.add(purchaseAmount); // Accumuler le montant total
        }

        var entity = new Order();
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        entity.setAmount(totalAmount);
        entity.setUtilisateur(utilisateur);
        var out = orderDAO.save(entity);
        log.info("Saved order: {}", out);
        return ResponseEntity.ok(orderResponse);
    }


    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request,@RequestParam("utilisateur")  String emailUtilisateur) {
        var orderId = request.getParameter("token");

        // Déclencher le processus de capture du paiement.
        try {
            payPalHttpClient.captureOrder(orderId);
            var out = orderDAO.findByPaypalOrderId(orderId);
            out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
            out.setPaymentDate(LocalDate.now());


            orderDAO.save(out);
            Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);

            Panier panier = utilisateur.getPanier();

            if (panier != null) {
                List<LignePanier> lignesPanier = panier.getLignesPanier();
                // Créer la commande à partir du panier et sauvegarder en base de données.

                commandeService.createAndSaveCommande(panier,lignesPanier);

                // Nettoyer le panier après la création de la commande.
                panierService.clearPanier(panier);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Panier introuvable");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la capture du paiement");
        }

        return ResponseEntity.ok().body("Paiement réussi");
    }

}
