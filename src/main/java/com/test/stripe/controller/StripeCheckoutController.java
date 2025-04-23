package com.test.stripe.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.test.stripe.request.CheckoutRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/checkout")
public class StripeCheckoutController {

    @Value("${stripe.currency:inr}")
    private String currency;

    @Value("${app.domain:http://localhost:8080}")
    private String domain;

    @PostMapping("/create-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest checkoutRequest) {
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(domain + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(domain + "/payment-cancel")
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount(checkoutRequest.getAmount().longValue())
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(checkoutRequest.getProductName())
                                                                            .setDescription(checkoutRequest.getDescription())
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.valueOf("ideal"))
                            .build();

            Session session = Session.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Checkout session creation failed: " + e.getMessage());
        }
    }
}