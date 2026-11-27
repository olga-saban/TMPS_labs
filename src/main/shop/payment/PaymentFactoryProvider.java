package main.shop.payment;

import java.util.List;
import java.util.Locale;

// picks the right factory by name
public final class PaymentFactoryProvider {
    private PaymentFactoryProvider() { }

    public static PaymentFactory getFactory(String type) {
        String key = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        switch (key) {
            case "card":   return new CardPaymentFactory();
            case "paypal": return new PayPalPaymentFactory();
            case "crypto": return new CryptoPaymentFactory();
            default: throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }

    public static List<Payable> createAll() {
        return List.of(
                getFactory("card").createPayment(),
                getFactory("paypal").createPayment(),
                getFactory("crypto").createPayment());
    }
}
