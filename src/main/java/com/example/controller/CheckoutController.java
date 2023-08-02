package com.example.controller;

import com.example.model.*;
import com.example.repository.OrderDAO;
import com.example.service.PayPalHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/checkout")
@Slf4j
public class CheckoutController {

    private final PayPalHttpClient payPalHttpClient;
    private final OrderDAO orderDAO;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO) throws Exception {
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8080/checkout/success");
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
        var out = orderDAO.save(entity);
        log.info("Saved order: {}", out);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) {
        var orderId = request.getParameter("token");
        var out = orderDAO.findByPaypalOrderId(orderId);
        out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
        out.setPaymentDate(LocalDate.now());
        orderDAO.save(out);
        return ResponseEntity.ok().body("Payment success");
    }

}
