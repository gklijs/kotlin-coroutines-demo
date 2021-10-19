package tech.gklijs

enum class ActionType(val action: IntAction) {
    DELAYED(Sup { d -> tech.gklijs.supplier.DelayedInt.get(d) }),
    FUTURE(Fut { d -> tech.gklijs.supplier.DelayedInt.getFuture(d) })
}