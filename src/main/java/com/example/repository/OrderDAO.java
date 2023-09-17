package com.example.repository;

import com.example.model.Commande;
import com.example.model.PaypalOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderDAO extends CrudRepository<PaypalOrder, Long> {
    PaypalOrder findByPaypalOrderId(String paypalOrderId);
    PaypalOrder findByCommande(Commande commande);

}