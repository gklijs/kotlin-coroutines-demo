package tech.gklijs.consumer;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

public class ConcurrentConsumer<T> implements PollingConsumer<T> {
    private final ScheduledFuture<?> future;
    private ConcurrentLinkedQueue<T> queue;

    public ConcurrentConsumer(int secondsPerItem, Supplier<T> supplier) {
        queue = new ConcurrentLinkedQueue<>();
        future = ConcurrentProducer.start(secondsPerItem, supplier, queue);
    }

    @Override
    public List<T> poll(Integer millis) {
        if (future.isCancelled() || future.isDone()) {
            throw new AlreadyClosedException();
        }
        //TODO
        return null;
    }

    @Override
    public void close() {
        future.cancel(false);
        queue.clear();
        queue = null;
    }
}
