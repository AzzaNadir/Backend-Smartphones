package com.example.controller;

import com.example.model.PasswordResetToken;
import com.example.model.TokenGenerator;
import com.example.model.Utilisateur;
import com.example.repository.UtilisateurRepository;
import com.example.service.PasswordResetTokenService;
import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/api")
public class PasswordResetController {
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/request-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String userEmail) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurParEmail(userEmail);
        if (utilisateur == null) {
            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
        }

        String token = TokenGenerator.generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, utilisateur, 10);
        passwordResetTokenService.save(passwordResetToken);

        sendResetPasswordEmail(userEmail, token);

        return ResponseEntity.ok("Un e-mail de réinitialisation de mot de passe a été envoyé.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null || isTokenExpired(passwordResetToken)) {
            return ResponseEntity.badRequest().body("Token de réinitialisation invalide ou expiré.");
        }

        Utilisateur utilisateur = passwordResetToken.getUtilisateur();

        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(utilisateur);

        passwordResetTokenService.delete(passwordResetToken);

        return ResponseEntity.ok("Le mot de passe a été réinitialisé avec succès.");
    }

    private void sendResetPasswordEmail(String userEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("azna2603@student.iepscf-uccle.be");
        message.setTo(userEmail);
        message.setSubject("Réinitialisation de mot de passe");
        String resetLink = "http://localhost:3000/ResetPasswordPage?token=" + token;
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);
        mailSender.send(message);
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        LocalDateTime expiryDateTime = passwordResetToken.getExpiryDateTime();
        return expiryDateTime.isBefore(LocalDateTime.now());
    }
}
