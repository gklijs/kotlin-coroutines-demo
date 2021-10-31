package tech.gklijs.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import static tech.gklijs.consumer.Util.delay;

public class ConcurrentConsumer<T> implements PollingConsumer<T> {
    private final ScheduledFuture<?> future;
    private ConcurrentLinkedQueue<T> queue;

    public ConcurrentConsumer(int millisPerItem, Supplier<T> supplier) {
        queue = new ConcurrentLinkedQueue<>();
        future = ConcurrentProducer.start(millisPerItem, supplier, queue);
    }

    @Override
    public List<T> poll(Integer millis) {
        if (future.isCancelled() || future.isDone()) {
            throw new AlreadyClosedException();
        }
        if (queue.isEmpty()) {
            delay(millis.longValue());
        }
        return getFromQueue();
    }

    private List<T> getFromQueue() {
        List<T> list = new ArrayList<>();
        T head = queue.poll();
        while (head != null) {
            list.add(head);
            head = queue.poll();
        }
        return list;
    }

    @Override
    public void close() {
        future.cancel(false);
        queue.clear();
        queue = null;
    }
}
