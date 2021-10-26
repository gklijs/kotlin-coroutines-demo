package tech.gklijs

import kotlinx.coroutines.delay
import tech.gklijs.consumer.PollingConsumer
import java.time.Instant
import java.util.concurrent.Future
import java.util.function.Consumer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Int.log() {
    println("${Instant.now()} - received number: ${
        this.toString().padStart(5, '0')
    } - logged from ${Thread.currentThread().name}")
}

fun IntAction.run(delay: Int) {
    when (this) {
        is GetSupplier -> this.value.invoke(delay).log()
        is GetFuture -> this.value.invoke(delay).get().log()
        is GetFutureWithCallBack -> this.value.invoke(delay) { x -> x.log() }.get()
        is GetConsumer -> this.value.invoke(delay).runTill(10)
    }
}

fun PollingConsumer<Int>.runTill(items: Int) {
    var consumed = 0
    while (consumed < items) {
        this.poll(100).forEach {
            consumed++
            it.log()
        }
    }
    this.close()
}

suspend fun IntAction.suspend(delay: Int) {
    when (this) {
        is GetSupplier -> this.value.invoke(delay).log()
        is GetFuture -> this.value.invoke(delay).eventuallyLog()
        is GetFutureWithCallBack -> this.value.dispatch(delay).log()
        is GetConsumer -> this.value.invoke(delay).suspendTill(10)
    }
}

suspend fun Future<Int>.eventuallyLog() {
    while (!this.isDone && !this.isCancelled) {
        delay(10)
    }
    this.runCatching { this.get().log() }
}

suspend inline fun ((Int, Consumer<Int>) -> Future<Int>).dispatch(delay: Int) =
    suspendCoroutine<Int> { continuation ->
        val callback = { x: Int -> continuation.resume(x) }
        this.invoke(delay, callback)
    }

suspend fun PollingConsumer<Int>.suspendTill(items: Int) {
    var consumed = 0
    while (consumed < items) {
        delay(100)
        this.poll(0).forEach {
            consumed++
            it.log()
        }
    }
    this.close()
}