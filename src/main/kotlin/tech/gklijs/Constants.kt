package tech.gklijs

object Constants {
    var consumeDelay: Int = 100
    var consumeAmount: Int = 10
    var futureDelay: Int = 10

    fun setAndPrintConsumeDelay(value: Int) {
        consumeDelay = value
        println("Set consume delay to $value.")
    }

    fun setAndPrintConsumeAmount(value: Int) {
        consumeAmount = value
        println("Set consume amount to $value.")
    }

    fun setAndPrintFutureDelay(value: Int) {
        futureDelay = value
        println("Set print delay to $value.")
    }
}