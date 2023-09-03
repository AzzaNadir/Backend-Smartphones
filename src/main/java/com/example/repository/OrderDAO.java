package com.example.repository;

import com.example.model.Commande;
import com.example.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderDAO extends CrudRepository<Order, Long> {
    Order findByPaypalOrderId(String paypalOrderId);
    Order findByCommande(Commande commande);

}