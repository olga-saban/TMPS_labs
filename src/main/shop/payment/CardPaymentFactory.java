package main.shop.payment;

public final class CardPaymentFactory implements PaymentFactory {
    @Override
    public Payable createPayment() { return new CardPayment(); }

    @Override
    public String getName() { return "Card"; }
}
