package tech.gklijs

enum class ActionType(val action: () -> Unit) {
    DELAYED({ println("received number: ${tech.gklijs.supplier.DelayedInt.get()}") })
}