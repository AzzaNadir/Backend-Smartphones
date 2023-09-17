package com.example.controller;

import com.example.model.*;
import com.example.repository.OrderDAO;
import com.example.repository.ProduitRepository;
import com.example.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@CrossOrigin
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
    private PaypalOrder order;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }

    @Transactional
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

        var entity = new PaypalOrder();
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        entity.setAmount(totalAmount);
        var out = orderDAO.save(entity);
        log.info("Saved order: {}", out);
        return ResponseEntity.ok(orderResponse);
    }

    @Transactional
    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request, @RequestParam("utilisateur") String emailUtilisateur) {
        var orderId = request.getParameter("token");

        // Déclencher le processus de capture du paiement.
        try {
            Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(emailUtilisateur);
            Panier panier = utilisateur.getPanier();

            if (panier == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Panier introuvable");
            }

            List<LignePanier> lignesPanier = panier.getLignesPanier();

            for (LignePanier lignePanier : lignesPanier) {
                Produit produit = lignePanier.getProduit();

                // Vérifier si le produit est encore en stock en interrogeant la base de données
                int stockDisponible = produitRepository.getQuantiteStockById(produit.getId());
                System.out.println("STOCK DISPONIBLE" + stockDisponible);
                if (stockDisponible < lignePanier.getQuantite()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Article(s) en rupture de stock.");
                }
            }

            var out = orderDAO.findByPaypalOrderId(orderId);
            out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
            out.setPaymentDateTime(LocalDateTime.now());
            orderDAO.save(out);

            commandeService.createAndSaveCommande(panier, lignesPanier);

            List<Commande> commandes = utilisateur.getCommandes();
            if (!commandes.isEmpty()) {
                Commande commande = commandes.get(commandes.size() - 1); // Récupérer la dernière commande
                out.setCommande(commande);
                // Nettoyer le panier après la création de la commande.
                panierService.clearPanier(panier);
                payPalHttpClient.captureOrder(orderId);
                sendOrderConfirmationEmail(emailUtilisateur, commande, utilisateur);
                return ResponseEntity.ok().body("Paiement réussi");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Commande introuvable");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la capture du paiement");
        }
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

    private String generateEmailContent(Commande commande, Utilisateur utilisateur) {
        BigDecimal totalAmountWithTax = BigDecimal.ZERO;
        String statusPayment = "";
        PaypalOrder orders = orderDAO.findByCommande(commande);
        totalAmountWithTax = orders.getAmount();
        statusPayment = orders.getPaypalOrderStatus();

        if (statusPayment.equals("APPROVED")) {
            statusPayment = "Payé";
        }


        // Calculer la TVA en supposant un taux de TVA de 20% (à adapter selon votre taux réel).
        BigDecimal taxRate = new BigDecimal("0.21"); // Exemple pour 20% de TVA
        BigDecimal taxAmount = totalAmountWithTax.multiply(taxRate);

        BigDecimal totalAmount = totalAmountWithTax.subtract(taxAmount);

        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Format pour les montants
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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
                .append("<p>Date de commande : " + commande.getDateTimeCommande().format(dateTimeFormatter) + "</p>")
                .append("<p>Statut de la commande : " + commande.getCommandeStatus() + "</p>")
                .append("<p>Statut du payment : " + statusPayment + "</p>");

        List<LigneCommande> lignesCommande = commande.getLignesCommande();
        emailContentBuilder.append("<h3>Détails des produits :</h3><ul>");
        for (LigneCommande ligneCommande : lignesCommande) {
            Produit produit = ligneCommande.getProduit();
            if (produit instanceof Smartphone) {
                Smartphone smartphone = (Smartphone) produit;
                emailContentBuilder.append("<li>Produit : " + smartphone.getMarque() + " " + smartphone.getModele() + " " + smartphone.getCouleur() + " " + smartphone.getTailleEcran() + "\"" + " " + smartphone.getStockage() + " " + smartphone.getMemoireRam() + "</li>")
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
