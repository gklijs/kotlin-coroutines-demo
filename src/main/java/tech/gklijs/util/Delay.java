package tech.gklijs.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Delay {
    private Delay() {
        //prevent instantiation
    }

    private static Timer timer = null;

    private static synchronized Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }

    public static synchronized void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static <T> Future<T> getDelayedFuture(int delay, Supplier<T> supplier, Consumer<T> callback) {
        CompletableFuture<T> future = new CompletableFuture<>();
        getTimer().schedule(new DelayedTask<>(future, supplier, callback), delay);
        return future;
    }

    public static <T> Future<T> getDelayedFuture(int delay, Supplier<T> supplier) {
        return getDelayedFuture(delay, supplier, null);
    }

    public static <T> T getDelayed(int delay, Supplier<T> supplier) {
        try {
            return getDelayedFuture(delay, supplier).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DelayedTask<T> extends TimerTask {
        private final CompletableFuture<T> future;
        private final Supplier<T> supplier;
        private final Consumer<T> callback;

        public DelayedTask(CompletableFuture<T> future, Supplier<T> supplier, Consumer<T> callback) {
            this.future = future;
            this.supplier = supplier;
            this.callback = callback;
        }

        @Override
        public void run() {
            T value = supplier.get();
            future.complete(value);
            if (callback != null) {
                callback.accept(value);
            }
        }
    }
}
