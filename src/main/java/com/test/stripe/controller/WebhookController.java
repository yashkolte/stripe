package com.test.stripe.controller;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payment")
public class WebhookController {

    private static final String WEBHOOK_SECRET = "your_webhook_secret_here";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, HttpServletRequest request) {
        try {
            Event event = Webhook.constructEvent(
                    payload,
                    request.getHeader("Stripe-Signature"),
                    WEBHOOK_SECRET
            );

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    // TODO: Handle payment success
                    break;
                case "payment_intent.payment_failed":
                    // TODO: Handle payment failure
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("Webhook received: " + event.getType());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
    }
}
