// strategy pattern
public class PayPalPayment implements Payable, Refundable {
    @Override
    public String getName() { return "PayPal"; }

    @Override
    public PaymentResult pay(double amount) {
        System.out.printf("[PayPal] Paid %.2f EUR%n", amount);
        return new PaymentResult(true, "PAYPAL-002");
    }

    @Override
    public void refund(double amount) {
        System.out.printf("[PayPal] Refunded %.2f EUR%n", amount);
    }
}
