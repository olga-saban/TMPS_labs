package main.shop.decorator;

import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

/** Decorator pattern - ConcreteDecorator: prints what is paid and the result. */
public class LoggingPayment extends PaymentDecorator {
    public LoggingPayment(Payable wrapped) { super(wrapped); }

    @Override
    public PaymentResult pay(double amount) {
        System.out.printf("[LOG] paying %.2f with %s%n", amount, getName());
        PaymentResult r = super.pay(amount);
        System.out.println("[LOG] result: " + (r.isSuccess() ? "OK " + r.getTransactionId() : "FAILED"));
        return r;
    }
}
