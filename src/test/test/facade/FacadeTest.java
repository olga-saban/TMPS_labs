package test.facade;

import org.junit.Test;
import test.Stubs.CountingPayment;
import main.shop.facade.CheckoutFacade;
import main.shop.model.Item;
import main.shop.model.Order2;
import main.shop.payment.PaymentResult;
import main.shop.proxy.LimitedPaymentProxy;

import java.util.List;

import static org.junit.Assert.*;

public class FacadeTest {
    @Test
    public void checkoutPaysOrderTotalAndRetriesOnce() {
        CountingPayment real = new CountingPayment(1);   // first try fails, retry succeeds
        Order2 order = new Order2(List.of(new Item("A", 10), new Item("B", 5.5)));
        PaymentResult r = new CheckoutFacade(5000).checkout(order, real);
        assertTrue(r.isSuccess());
        assertEquals(2, real.calls);
        assertEquals(15.5, real.lastAmount, 1e-9);
    }

    @Test
    public void checkoutOverLimitIsDenied() {
        CountingPayment real = new CountingPayment(0);
        Order2 order = new Order2(List.of(new Item("PC", 6000)));
        PaymentResult r = new CheckoutFacade(5000).checkout(order, real);
        assertFalse(r.isSuccess());
        assertEquals(LimitedPaymentProxy.DENIED, r.getTransactionId());
        assertEquals(0, real.calls);
    }
}
