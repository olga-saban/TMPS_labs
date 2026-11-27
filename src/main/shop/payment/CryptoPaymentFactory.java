package main.shop.payment;

public final class CryptoPaymentFactory implements PaymentFactory {
    @Override
    public Payable createPayment() { return new CryptoPayment(); }

    @Override
    public String getName() { return "Crypto"; }
}
