package test.proxy;

import org.junit.Test;
import test.Stubs.CountingPayment;
import main.shop.payment.PaymentResult;
import main.shop.proxy.LimitedPaymentProxy;

import static org.junit.Assert.*;

public class ProxyTest {
    @Test
    public void amountWithinLimitIsDelegated() {
        CountingPayment real = new CountingPayment(0);
        PaymentResult r = new LimitedPaymentProxy(real, 100).pay(100);
        assertTrue(r.isSuccess());
        assertEquals(1, real.calls);
        assertEquals(100, real.lastAmount, 1e-9);
    }

    @Test
    public void amountOverLimitIsBlockedWithoutCallingReal() {
        CountingPayment real = new CountingPayment(0);
        PaymentResult r = new LimitedPaymentProxy(real, 100).pay(100.01);
        assertFalse(r.isSuccess());
        assertEquals(LimitedPaymentProxy.DENIED, r.getTransactionId());
        assertEquals(0, real.calls);
    }
}
