package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paypal_orders")
@Data
@Component
public class PaypalOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "paypal_order_id")
    private String paypalOrderId;
    @Column(name = "paypal_order_status")
    private String paypalOrderStatus;
    @Column(name = "payment_dateTime")
    private LocalDateTime paymentDateTime;
    @Column(name = "amount")
    private BigDecimal amount;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "commande_id") // Clé étrangère vers la commande associée
    private Commande commande; // Ajoutez cette référence à la commande
}
