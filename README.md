# Stripe Payment Integration with Spring Boot

A comprehensive Spring Boot application that demonstrates integration with Stripe payment gateway, featuring credit card payments with 3D Secure authentication, support for multiple currencies and countries, and Stripe Checkout.

## Features

- **Direct Card Payments**: Process credit/debit card payments directly within your application
- **3D Secure Authentication**: Support for 3D Secure authentication required in many regions
- **Multi-Currency Support**: Accept payments in various currencies (USD, EUR, GBP, INR, etc.)
- **Country-Specific Handling**: Special handling for regional requirements (India, Europe, etc.)
- **Stripe Checkout Integration**: Alternative payment flow using Stripe's hosted checkout page
- **Webhook Processing**: Handle asynchronous payment events from Stripe

## Prerequisites

- Java 21 or higher
- Maven 3.6.3 or higher
- [Stripe Account](https://dashboard.stripe.com/register) (Test or Live)
- Stripe API Keys (available in your Stripe Dashboard)

## Project Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/stripe-spring-integration.git
   cd stripe-spring-integration
   ```

2. **Configure Stripe API Keys**
   
   Update `src/main/resources/application.properties` with your Stripe API keys:
   ```properties
   stripe.secretKey=sk_test_your_secret_key
   stripe.webhookSecret=whsec_your_webhook_secret
   stripe.currency=usd
   ```
   Note: Never commit your real API keys to version control. Consider using environment variables or a secure configuration system for production.

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   The application will start on `http://localhost:8080` by default.

## Using the Application

1. **Access the payment page**
   
   Open your browser and navigate to `http://localhost:8080`

2. **Direct Card Payment**
   - Enter an amount
   - Select currency and country
   - Provide an email address
   - Enter test card details:
     - Success: 4242 4242 4242 4242
     - 3D Secure: 4000 0000 0000 3220
     - Failure: 4000 0000 0000 9995
   - Complete the payment flow

3. **Stripe Checkout**
   - Switch to the "Stripe Checkout" tab
   - Enter amount and product details
   - Click "Continue to Checkout"
   - Complete payment on Stripe's hosted page

## Test Cards

Stripe provides several test cards you can use:

| Card Number | Description |
|-------------|-------------|
| 4242 4242 4242 4242 | Succeeds and doesn't require authentication |
| 4000 0000 0000 3220 | Requires 3D Secure authentication |
| 4000 0000 0000 9995 | Always fails with a decline code of insufficient_funds |

For 3D Secure test cards:
- Use any future expiration date
- Use any 3-digit CVC
- Use any value for other form fields

## Country-Specific Behaviors

- **India**: Uses mandatory 3D Secure for INR payments as per RBI regulations
- **European Countries**: Configured for Strong Customer Authentication (SCA) compliance
- **Other Countries**: Standard payment flow with appropriate local methods

## API Endpoints

### Payment API

- **POST /api/payment/create-payment-intent**
  - Creates a payment intent for client-side payment processing
  - Request Body: 
    ```json
    {
      "amount": 1000,
      "currency": "usd",
      "country": "US",
      "customerEmail": "customer@example.com"
    }
    ```
  - Response: Client secret for Stripe Elements

### Checkout API

- **POST /api/checkout/create-session**
  - Creates a Stripe Checkout session
  - Request Body:
    ```json
    {
      "amount": 1000,
      "productName": "Product Name",
      "description": "Product description",
      "customerEmail": "customer@example.com"
    }
    ```
  - Response: Stripe Checkout URL

### Webhook API

- **POST /api/payment/webhook**
  - Processes Stripe webhook events
  - Requires Stripe-Signature header for validation

## Webhook Setup

To handle asynchronous events (like payment confirmations), set up webhooks:

1. Install [Stripe CLI](https://stripe.com/docs/stripe-cli) for local testing
2. Forward events to your local server:
   ```
   stripe listen --forward-to localhost:8080/api/payment/webhook
   ```
3. For production, configure your webhook endpoint in the Stripe Dashboard

## Common Issues and Solutions

- **CORS Errors**: If you encounter CORS issues, ensure your frontend origin is allowed in WebConfig.java
- **3D Secure Not Working**: Check if your test card supports 3D Secure and your device has supported browsers
- **Webhook Signature Failure**: Ensure you're using the correct webhook secret and properly forwarding the Stripe-Signature header

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── test/
│   │           └── stripe/
│   │               ├── config/             # Configuration classes
│   │               ├── controller/         # REST controllers
│   │               └── request/            # Request models
│   └── resources/
│       ├── static/                       # Frontend resources
│       └── application.properties        # Application configuration
```

## Dependencies

- Spring Boot 3.2.0
- Stripe Java SDK 29.0.0
- Project Lombok
- Spring Validation

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.