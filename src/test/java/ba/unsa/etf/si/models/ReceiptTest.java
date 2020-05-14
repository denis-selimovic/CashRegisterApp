package ba.unsa.etf.si.models;

import ba.unsa.etf.si.models.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ReceiptTest {

    @Test
    public void testCase() {
        Receipt receipt = new Receipt();
        receipt.setPaymentMethod(PaymentMethod.CASH);
        assertSame(receipt.getPaymentMethod(), PaymentMethod.CASH);
    }
}