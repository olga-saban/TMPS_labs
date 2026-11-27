package main.shop.facade;

import main.shop.checkout.CheckoutService;
import main.shop.decorator.LoggingPayment;
import main.shop.decorator.RetryPayment;
import main.shop.model.Order2;
import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;
import main.shop.proxy.LimitedPaymentProxy;

/**
 * Facade pattern: one simple call for the window. It builds the payment chain
 * Proxy -> Decorator -> Decorator -> real payment and runs the checkout; it has no business rules itself.
 */
public class CheckoutFacade {
    private final double limit;

    public CheckoutFacade(double limit) { this.limit = limit; }

    public PaymentResult checkout(Order2 order, Payable payment) {
        Payable chain = new LimitedPaymentProxy(
                new LoggingPayment(new RetryPayment(payment, 2)), limit);
        return new CheckoutService(chain).checkout(order);   // client code uses only the Payable interface
    }
}
