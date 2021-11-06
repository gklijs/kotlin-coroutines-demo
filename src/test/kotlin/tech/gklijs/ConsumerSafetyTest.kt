package tech.gklijs

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tech.gklijs.consumer.ConcurrentException
import kotlin.test.assertTrue

class ConsumerSafetyTest {
    private val delay = 1

    @Test
    fun unsafeConsumerTest() {
        val action = ActionType.UNSAFE_CONSUMER.action
        assertTrue(action is GetConsumer)
        val consumer = action.value.invoke(1)
        consumer.poll(delay)
        assertThrows<ConcurrentException> {
            runBlocking(kotlinx.coroutines.Dispatchers.Default) {
                consumer.poll(delay)
            }
        }
    }

    @Test
    fun safeConsumerTest() {
        val action = ActionType.SAFE_CONSUMER.action
        assertTrue(action is GetConsumer)
        val consumer = action.value.invoke(1)
        consumer.poll(delay)
        runBlocking(kotlinx.coroutines.Dispatchers.Default) {
            consumer.poll(delay)
        }
    }
}