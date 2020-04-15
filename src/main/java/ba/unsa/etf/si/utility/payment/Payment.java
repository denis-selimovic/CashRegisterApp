package ba.unsa.etf.si.utility.payment;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class Payment {

    private Payment() {}

    public static void cashPayment(Runnable payment, BiFunction<? super Void, Throwable, ? super Void> handle) {
        CompletableFuture.runAsync(payment).handle(handle);
    }

    public static void qrPayment(Runnable payment, Runnable gui, Runnable sleep, BiFunction<? super Void, Throwable, ? super Void> handle) {
        CompletableFuture.runAsync(payment).thenRunAsync(gui).thenRunAsync(sleep).handle(handle);
    }

    public static void creditCardPayment(boolean valid, Runnable error, Runnable payment, BiFunction<? super Void, Throwable, ? super  Void> handle) {
        if(valid) error.run();
        else CompletableFuture.runAsync(payment).handle(handle);
    }
}
