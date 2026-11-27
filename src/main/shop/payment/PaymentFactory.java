package main.shop.payment;

// Factory Method: each factory creates one kind of payment
public interface PaymentFactory {
    Payable createPayment();
    String getName();
}
