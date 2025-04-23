package com.test.stripe.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.test.stripe.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    @Value("${stripe.currency:usd}")
    private String defaultCurrency;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Use the currency from the request if provided, otherwise use default
            String currency = (paymentRequest.getCurrency() != null && !paymentRequest.getCurrency().isEmpty()) 
                ? paymentRequest.getCurrency().toLowerCase() : defaultCurrency;
            
            logger.info("Creating payment intent for amount: " + paymentRequest.getAmount() + 
                        ", currency: " + currency + 
                        ", country: " + paymentRequest.getCountry());
            
            // Start building the PaymentIntent
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().longValue())
                    .setCurrency(currency);
            
            // Add automatic payment methods for all countries
            paramsBuilder.setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            );
            
            // Apply country-specific additional configurations if needed
            if (paymentRequest.getCountry() != null && !paymentRequest.getCountry().isEmpty()) {
                String country = paymentRequest.getCountry().toUpperCase();
                
                // For India, require 3D Secure
                if ("IN".equals(country) && "inr".equals(currency)) {
                    paramsBuilder.setPaymentMethodOptions(
                            PaymentIntentCreateParams.PaymentMethodOptions.builder()
                                    .setCard(
                                            PaymentIntentCreateParams.PaymentMethodOptions.Card.builder()
                                                    .setRequestThreeDSecure(PaymentIntentCreateParams.PaymentMethodOptions.Card.RequestThreeDSecure.ANY)
                                                    .build()
                                    )
                                    .build()
                    );
                }
            }
            
            // Create the PaymentIntent
            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            logger.info("Successfully created payment intent with ID: " + paymentIntent.getId());

            // Return the client secret
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.log(Level.SEVERE, "Stripe API error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment initialization failed: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // Helper method to check if a country is in Europe (for SCA requirements)
    private boolean isEuropeanCountry(String countryCode) {
        return countryCode.matches("AT|BE|BG|HR|CY|CZ|DK|EE|FI|FR|DE|GR|HU|IE|IT|LV|LT|LU|MT|NL|PL|PT|RO|SK|SI|ES|SE|GB");
    }
}