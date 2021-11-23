package tech.gklijs

import org.junit.jupiter.api.Test
import tech.gklijs.supplier.DelayedInt
import kotlin.test.assertEquals

class RunTypeTest {

    private val action = ActionType.DELAYED
    private val delay = 1
    private val times: Int = 3

    private fun genericTest(runType: RunType) {
        val start = DelayedInt.get()
        runType.run.invoke(delay, times, action)
        val end = DelayedInt.get()
        assertEquals(3, end - start)
    }

    @Test
    fun sameThreadTest() {
        genericTest(RunType.SAME_THREAD)
    }

    @Test
    fun sameThreadComplicatedTest() {
        genericTest(RunType.SAME_THREAD_COMPLICATED)
    }

    @Test
    fun threadPoolTest() {
        genericTest(RunType.THREAD_POOL)
    }

    @Test
    fun suspendedTest() {
        genericTest(RunType.SUSPENDED)
    }

    @Test
    fun coroutineDefaultTest() {
        genericTest(RunType.SUSPENDED_DEFAULT)
    }

    @Test
    fun coroutineUnconfinedTest() {
        genericTest(RunType.SUSPENDED_UNCONFINED)
    }

    @Test
    fun suspendedBlockingTest() {
        genericTest(RunType.SUSPENDED_BLOCKING)
    }

    @Test
    fun coroutineDefaultBlockingTest() {
        genericTest(RunType.SUSPENDED_DEFAULT_BLOCKING)
    }

    @Test
    fun coroutineDefaultLimitedParallelism() {
        genericTest(RunType.SUSPENDED_DEFAULT_LIMITED_PARALLELISM)
    }

    @Test
    fun suspendedNewTest() {
        genericTest(RunType.SUSPENDED_NEW)
    }
}
