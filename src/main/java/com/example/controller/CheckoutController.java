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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private Order order;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }


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
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request, @RequestParam("utilisateur") String emailUtilisateur) {
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

                commandeService.createAndSaveCommande(panier, lignesPanier);

                // Nettoyer le panier après la création de la commande.
                panierService.clearPanier(panier);
                List<Commande> commandes = utilisateur.getCommandes();
                if (!commandes.isEmpty()) {
                    Commande commande = commandes.get(commandes.size() - 1); // Récupérer la dernière commande
                    sendOrderConfirmationEmail(emailUtilisateur, commande, utilisateur);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commande introuvable");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Panier introuvable");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la capture du paiement");
        }

        return ResponseEntity.ok().body("Paiement réussi");
    }

    private void sendOrderConfirmationEmail(String recipientEmail, Commande commande, Utilisateur utilisateur) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        message.setFrom("azna2603@student.iepscf-uccle.be");

        // Configurez l'objet, le destinataire, etc.
        helper.setSubject("Confirmation de commande");
        helper.setTo(recipientEmail);

        // Créez le contenu de l'e-mail en HTML.
        String emailContent = generateEmailContent(commande, utilisateur);

        helper.setText(emailContent, true);

        // Envoyez l'e-mail.
        mailSender.send(message);
    }

//    private String generateEmailContent(Commande commande, Utilisateur utilisateur) {
//        BigDecimal totalAmountWithTax = BigDecimal.ZERO;
//        List<Order> orders = utilisateur.getOrders();
//        if (!orders.isEmpty()) {
//            Order order = orders.get(orders.size() - 1);
//            totalAmountWithTax = order.getAmount();
//        }
//        // Calculer la TVA en supposant un taux de TVA de 20% (à adapter selon votre taux réel).
//        BigDecimal taxRate = new BigDecimal("0.21"); // Exemple pour 20% de TVA
//        BigDecimal taxAmount = totalAmountWithTax.multiply(taxRate);
//
//        BigDecimal totalAmount = totalAmountWithTax.subtract(taxAmount);
//
//        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Format pour les montants
//
//        String emailContent = "<html><body>"
//                + "<h2>Confirmation de commande</h2>"
//                + "<p>Merci d'avoir effectué votre commande. Voici les détails :</p>"
//                + "<p>Sous-total : " + decimalFormat.format(totalAmount) + " EUR</p>"
//                + "<p>TVA : " + decimalFormat.format(taxAmount) + " EUR</p>"
//                + "<p>Total : " + decimalFormat.format(totalAmountWithTax) + " EUR</p>"
//                + "</body></html>";
//
//        return emailContent;
//    }


    private String generateEmailContent(Commande commande, Utilisateur utilisateur) {
        BigDecimal totalAmountWithTax = BigDecimal.ZERO;
        String statusPayment = "";
        List<Order> orders = utilisateur.getOrders();
        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            totalAmountWithTax = order.getAmount();
            statusPayment= order.getPaypalOrderStatus();
        }
        if (statusPayment=="APPROVED"){
            statusPayment= "Payé";
        }
        // Calculer la TVA en supposant un taux de TVA de 20% (à adapter selon votre taux réel).
        BigDecimal taxRate = new BigDecimal("0.21"); // Exemple pour 20% de TVA
        BigDecimal taxAmount = totalAmountWithTax.multiply(taxRate);

        BigDecimal totalAmount = totalAmountWithTax.subtract(taxAmount);

        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Format pour les montants
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder emailContentBuilder = new StringBuilder();
        emailContentBuilder.append("<html><body>")
                .append("<h2>Confirmation de commande</h2>")
                .append("<p>Merci d'avoir effectué votre commande. Voici les détails :</p>")
                .append("<p>Sous-total : " + decimalFormat.format(totalAmount) + " €</p>")
                .append("<p>TVA : " + decimalFormat.format(taxAmount) + " €</p>")
                .append("<p>Total : " + decimalFormat.format(totalAmountWithTax) + " €</p>");
        // Ajouter les informations de l'utilisateur
        emailContentBuilder.append("<h3>Informations de livraison :</h3>")
                .append("<p>Nom : " + utilisateur.getNom() + "</p>")
                .append("<p>Prénom : " + utilisateur.getPrenom() + "</p>")
                .append("<p>Adresse de livraison : " + utilisateur.getAdresse() + "</p>")
                .append("<p>Numéro de téléphone : " + utilisateur.getNumeroDeTelephone() + "</p>");
        // Ajouter les détails de la commande et les lignes de commande
        emailContentBuilder.append("<h3>Détails de la commande :</h3>")
                .append("<p>Date de commande : " + commande.getDateCommande().format(dateFormatter) + "</p>")
                .append("<p>Statut de la commande : " + commande.getCommandeStatus() + "</p>")
                .append("<p>Statut du payment : " +  statusPayment+ "</p>");

        List<LigneCommande> lignesCommande = commande.getLignesCommande();
        emailContentBuilder.append("<h3>Détails des produits :</h3><ul>");
        for (LigneCommande ligneCommande : lignesCommande) {
            Produit produit = ligneCommande.getProduit();
            if (produit instanceof Smartphone) {
                Smartphone smartphone = (Smartphone) produit;
                emailContentBuilder.append("<li>Produit : " + smartphone.getMarque() + " " + smartphone.getModele() + " " + smartphone.getCouleur() + " " + smartphone.getStockage() + " " + smartphone.getMemoireRam() + "</li>")
                        .append("<li>Quantité : " + ligneCommande.getQuantite() + "</li>")
                        .append("<li>Prix unitaire : " + ligneCommande.getPrixUnitaire() + " € TVAC</li>")
                        .append("<li>Total : " + ligneCommande.getTotalLigne() + " € TVAC</li><br>");
            } else {
                // Ajouter des informations pour d'autres types de produits si nécessaire
            }
        }
        emailContentBuilder.append("</ul></body></html>");

        return emailContentBuilder.toString();
    }


}
