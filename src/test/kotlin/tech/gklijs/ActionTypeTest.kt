package tech.gklijs

import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertTrue

class ActionTypeTest {
    private val delay = 50

    @Test
    fun delayedTest() {
        val start = Instant.now()
        val action = ActionType.DELAYED.action
        assertTrue(action is GetSupplier)
        action.value.invoke(delay)
        val end = Instant.now()
        val duration = Duration.between(start, end).toMillis()
        assertTrue(duration >= delay)
    }

    @Test
    fun futureTest() {
        val start = Instant.now()
        val action = ActionType.FUTURE.action
        assertTrue(action is GetFuture)
        action.value.invoke(delay).get()
        val end = Instant.now()
        val duration = Duration.between(start, end).toMillis()
        assertTrue(duration >= delay)
    }

    @Test
    fun callableTest() {
        val start = Instant.now()
        val action = ActionType.CALLABLE.action
        assertTrue(action is GetFutureWithCallBack)
        action.value.invoke(delay) {}.get()
        val end = Instant.now()
        val duration = Duration.between(start, end).toMillis()
        assertTrue(duration >= delay)
    }

    @Test
    fun unsafeConsumerTest() {
        val start = Instant.now()
        val action = ActionType.UNSAFE_CONSUMER.action
        assertTrue(action is GetConsumer)
        val consumer = action.value.invoke(delay)
        val pollResult = consumer.poll(delay)
        assertTrue(pollResult.isNotEmpty())
        val end = Instant.now()
        val duration = Duration.between(start, end).toMillis()
        assertTrue(duration >= delay)
    }

    @Test
    fun safeConsumerTest() {
        val start = Instant.now()
        val action = ActionType.SAFE_CONSUMER.action
        assertTrue(action is GetConsumer)
        val consumer = action.value.invoke(delay)
        //adding 10 ms is not nice, but takes care of the stuff needed to initiate this consumer
        val pollResult = consumer.poll(delay + 10)
        assertTrue(pollResult.isNotEmpty())
        val end = Instant.now()
        val duration = Duration.between(start, end).toMillis()
        assertTrue(duration >= delay)
    }
}