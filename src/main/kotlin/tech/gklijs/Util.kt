package tech.gklijs

import kotlinx.coroutines.delay
import java.util.concurrent.Future

fun Int.log() {
    println("received number: $this")
}

fun IntAction.run(delay: Int) {
    when (this) {
        is Sup -> this.value.invoke(delay).log()
        is Fut -> this.value.invoke(delay).get().log()
        is Pub -> TODO()
    }
}

suspend fun IntAction.suspend(delay: Int) {
    when (this) {
        is Sup -> this.value.invoke(delay).log()
        is Fut -> this.value.invoke(delay).eventuallyLog()
        is Pub -> TODO()
    }
}

suspend fun Future<Int>.eventuallyLog() {
    while (!this.isDone && !this.isCancelled) {
        delay(10)
    }
    this.runCatching { this.get().log() }
}