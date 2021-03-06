package tech.gklijs.consumer;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class ConcurrentProducer {
    private static ScheduledExecutorService executor = null;

    private ConcurrentProducer() {
        //prevent instantiation
    }

    private static synchronized ScheduledExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(1);
        }
        return executor;
    }

    static <T> ScheduledFuture<?> start(int millisPerItem, Supplier<T> supplier, Queue<T> queue) {
        return getExecutor().scheduleAtFixedRate(() -> queue.add(supplier.get()), millisPerItem, millisPerItem, TimeUnit.MILLISECONDS);
    }
}
