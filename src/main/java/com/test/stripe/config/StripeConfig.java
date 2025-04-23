package com.test.stripe.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Bean
    public String initStripe() {
        Stripe.apiKey = secretKey;
        return "Stripe initialized with API key";
    }
}