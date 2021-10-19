package tech.gklijs

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
        is Fut -> this.value.invoke(delay).get().log()
        is Pub -> TODO()
    }
}