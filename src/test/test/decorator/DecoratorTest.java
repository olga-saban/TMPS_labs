package test.decorator;

import org.junit.Test;
import test.Stubs.CountingPayment;
import main.shop.decorator.LoggingPayment;
import main.shop.decorator.RetryPayment;
import main.shop.payment.Payable;
import main.shop.payment.PaymentResult;

import static org.junit.Assert.*;

public class DecoratorTest {
    @Test
    public void retryRepeatsUntilSuccess() {
        CountingPayment real = new CountingPayment(2);
        PaymentResult r = new RetryPayment(real, 3).pay(10);
        assertTrue(r.isSuccess());
        assertEquals(3, real.calls);
    }

    @Test
    public void retryStopsAfterMaxAttempts() {
        CountingPayment real = new CountingPayment(5);
        PaymentResult r = new RetryPayment(real, 2).pay(10);
        assertFalse(r.isSuccess());
        assertEquals(2, real.calls);
    }

    @Test
    public void decoratorsStackAndKeepInterface() {
        CountingPayment real = new CountingPayment(1);
        Payable chain = new LoggingPayment(new RetryPayment(real, 2));
        PaymentResult r = chain.pay(42.5);

        assertTrue(r.isSuccess());
        assertTrue(chain instanceof Payable);
        assertEquals("Stub", chain.getName());
        assertEquals(2, real.calls);
        assertEquals(42.5, real.lastAmount, 1e-9);
        assertEquals("OK-2", r.getTransactionId());
    }
}
