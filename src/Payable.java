//abstraction (Dependency inversion)
//new payment method (Open Closed)

public interface Payable {
    String getName();
    PaymentResult pay(double amount);
}
