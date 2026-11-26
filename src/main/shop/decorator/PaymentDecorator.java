package main.shop.decorator;

import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

/** Decorator pattern - base Decorator: is a Payable and has a Payable. */
public abstract class PaymentDecorator implements Payable {
    protected final Payable wrapped;

    protected PaymentDecorator(Payable wrapped) { this.wrapped = wrapped; }

    @Override public String getName() { return wrapped.getName(); }

    @Override public PaymentResult pay(double amount) { return wrapped.pay(amount); }
}
