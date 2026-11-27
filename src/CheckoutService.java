//only coordinates the payment (Single Responsibility)
//depends on the Payable interface (Dependency inversion)
public class CheckoutService {
    private final Payable payment;

    public CheckoutService(Payable payment) {
        this.payment = payment;
    }

    public PaymentResult checkout(Order2 order) {
        return payment.pay(order.getTotal());
    }
}
