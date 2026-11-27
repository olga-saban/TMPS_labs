package main.shop.app;

import main.shop.facade.CheckoutFacade;
import main.shop.model.Cart2;
import main.shop.model.Item;
import main.shop.model.Order2;
import main.shop.payment.Payable;
import main.shop.payment.PaymentFactoryProvider;
import main.shop.payment.PaymentResult;
import main.shop.receipt.ReceiptPrinter2;

/** Path: client -> Facade -> Proxy -> Decorators -> real payment (made by Factory Method). */
public class ConsoleDemo {
    public static void main(String[] args) {
        run("card", new Item("Keyboard", 59.99), 2);
        run("paypal", new Item("Desktop PC", 1199.99), 5);   // 5999.95 > limit 5000: blocked by the proxy
    }

    private static void run(String method, Item item, int qty) {
        System.out.println("\n=== " + qty + " x " + item.getName() + " by " + method + " ===");
        Cart2 cart = new Cart2();
        for (int i = 0; i < qty; i++) cart.add(item);
        Order2 order = new Order2(cart.toItems());
        Payable payment = PaymentFactoryProvider.getFactory(method).createPayment();
        PaymentResult result = new CheckoutFacade(5000).checkout(order, payment);
        System.out.print(ReceiptPrinter2.getInstance().format(order, payment, result));
    }
}
