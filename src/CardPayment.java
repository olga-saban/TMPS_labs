//strategy pattern
public class CardPayment implements Payable, Refundable {
    @Override
    public String getName() { return "Card"; }

    @Override
    public PaymentResult pay(double amount) {
        System.out.printf("[Card] Charged %.2f EUR%n", amount);
        return new PaymentResult(true, "CARD-001");
    }

    @Override
    public void refund(double amount) {
        System.out.printf("[Card] Refunded %.2f EUR%n", amount);
    }
}
