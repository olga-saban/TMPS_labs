package main.shop.payment;

public final class PayPalPaymentFactory implements PaymentFactory {
    @Override
    public Payable createPayment() { return new PayPalPayment(); }

    @Override
    public String getName() { return "PayPal"; }
}
