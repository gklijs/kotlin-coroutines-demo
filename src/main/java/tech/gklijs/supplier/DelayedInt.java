package tech.gklijs.supplier;

import tech.gklijs.util.Delay;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public final class DelayedInt {
    private DelayedInt() {
        //prevent instantiation
    }

    private static final AtomicInteger atom = new AtomicInteger(0);

    public static Integer get(Integer delay) {
        return Delay.getDelayed(delay, atom::getAndIncrement);
    }

    public static Future<Integer> getFuture(Integer delay) {
        return Delay.getDelayedFuture(delay, atom::getAndIncrement);
    }
}
