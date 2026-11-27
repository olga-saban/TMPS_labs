package main.shop.decorator;

import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

/** Decorator pattern - ConcreteDecorator: tries again when the payment fails. */
public class RetryPayment extends PaymentDecorator {
    private final int attempts;

    public RetryPayment(Payable wrapped, int attempts) {
        super(wrapped);
        this.attempts = attempts;
    }

    @Override
    public PaymentResult pay(double amount) {
        PaymentResult r = super.pay(amount);
        for (int i = 1; i < attempts && !r.isSuccess(); i++) r = super.pay(amount);
        return r;
    }
}
