package tech.gklijs

import kotlinx.coroutines.delay
import java.util.concurrent.Future
import java.util.function.Consumer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Int.log() {
    println("received number: $this")
}

fun IntAction.run(delay: Int) {
    when (this) {
        is Sup -> this.value.invoke(delay).log()
        is Fut -> this.value.invoke(delay).get().log()
        is Cal -> this.value.invoke(delay) { x -> x.log() }.get()
    }
}

suspend fun IntAction.suspend(delay: Int) {
    when (this) {
        is Sup -> this.value.invoke(delay).log()
        is Fut -> this.value.invoke(delay).eventuallyLog()
        is Cal -> this.value.dispatch(delay).log()
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