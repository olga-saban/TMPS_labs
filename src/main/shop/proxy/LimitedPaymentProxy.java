package main.shop.proxy;

import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

/** Proxy pattern - protection proxy: same Payable interface, blocks too big amounts. */
public class LimitedPaymentProxy implements Payable {
    public static final String DENIED = "DENIED";

    private final Payable real;
    private final double limit;

    public LimitedPaymentProxy(Payable real, double limit) {
        this.real = real;
        this.limit = limit;
    }

    @Override public String getName() { return real.getName(); }

    @Override
    public PaymentResult pay(double amount) {
        if (amount > limit) {                               // access check
            System.out.printf("[Proxy] %.2f blocked (limit %.2f)%n", amount, limit);
            return new PaymentResult(false, DENIED);        // real object is not called
        }
        return real.pay(amount);                            // otherwise delegate unchanged
    }
}
