package test;

import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

/** Stub real payment: fails the first `failures` calls and counts calls. */
public class Stubs {
    public static class CountingPayment implements Payable {
        public int calls;
        public double lastAmount;
        private final int failures;
        public CountingPayment(int failures) { this.failures = failures; }
        public String getName() { return "Stub"; }
        public PaymentResult pay(double amount) {
            calls++;
            lastAmount = amount;
            return calls > failures ? new PaymentResult(true, "OK-" + calls) : new PaymentResult(false, "FAIL");
        }
    }
}
