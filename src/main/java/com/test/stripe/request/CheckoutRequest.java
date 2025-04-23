package com.test.stripe.request;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Integer amount;
    private String productName;
    private String description;
    private String customerEmail;
}