package tech.gklijs.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public final class Delay {
    static Timer timer = new Timer();

    private Delay() {
        //prevent instantiation
    }

    public static void stop(){
        timer.cancel();
    }

    public static <T> T getDelayed(int seconds, Supplier<T> supplier){
        CompletableFuture<T> future = new CompletableFuture<>();
        timer.schedule(new DelayedTask<>(future, supplier), seconds * 1_000L);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DelayedTask<T> extends TimerTask {
        private final CompletableFuture<T> future;
        private final Supplier<T> supplier;

        public DelayedTask(CompletableFuture<T> future, Supplier<T> supplier) {
            this.future = future;
            this.supplier = supplier;
        }

        @Override
        public void run() {
            future.completeAsync(supplier);
        }
    }
}
