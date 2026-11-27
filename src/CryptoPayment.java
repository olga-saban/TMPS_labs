//strategy pattern
//doesnt implement Refundable
public class CryptoPayment implements Payable {
    @Override
    public String getName() { return "Crypto"; }

    @Override
    public PaymentResult pay(double amount) {
        System.out.printf("[Crypto] Transferred %.2f EUR%n", amount);
        return new PaymentResult(true, "CRYPTO-003");
    }
}
