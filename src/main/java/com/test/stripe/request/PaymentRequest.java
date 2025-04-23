package com.test.stripe.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer amount;
    private String currency;
    private String description;
    private String customerEmail;
    private String country;  // Added country field
}