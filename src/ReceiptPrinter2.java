import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

//only builds and prints receipts (Single Responsibility)
//Singleton: a shared receipt formatter/print service for the entire app
public class ReceiptPrinter2 {
    private static final class Holder {
        private static final ReceiptPrinter2 INSTANCE = new ReceiptPrinter2();
    }

    private static final String LINE = "------------------------------------";

    private ReceiptPrinter2() {
    }

    public static ReceiptPrinter2 getInstance() {
        return Holder.INSTANCE;
    }

    public String format(Order2 order, Payable payment, PaymentResult result) {
        return format(order, payment, result, false);
    }

    public String format(Order2 order, Payable payment, PaymentResult result, boolean refunded) {
        Map<Item, Integer> lines = new LinkedHashMap<>();
        for (Item item : order.getItems()) lines.merge(item, 1, Integer::sum);

        String status = !result.isSuccess() ? "FAILED" : refunded ? "REFUNDED" : "PAID";

        StringBuilder sb = new StringBuilder();
        sb.append("--- RECEIPT ---\n");
        sb.append("Order:    ").append(order.getId()).append('\n');
        sb.append("Method:   ").append(payment.getName()).append('\n');
        sb.append("Tx ID:    ").append(result.getTransactionId()).append('\n');
        sb.append(LINE).append('\n');
        for (Map.Entry<Item, Integer> e : lines.entrySet()) {
            sb.append(String.format(Locale.US, "%-22s %2d x %8.2f%n",
                    e.getKey().getName(), e.getValue(), e.getKey().getPrice()).replace("\r", ""));
        }
        sb.append(LINE).append('\n');
        sb.append(String.format(Locale.US, "Amount:   %.2f EUR%n", order.getTotal()).replace("\r", ""));
        sb.append("Status:   ").append(status).append('\n');
        sb.append(LINE).append('\n');
        return sb.toString();
    }

    public void print(Order2 order, Payable payment, PaymentResult result) {
        System.out.print(format(order, payment, result));
    }
}
