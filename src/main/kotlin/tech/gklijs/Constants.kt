package tech.gklijs

object Constants {
    var consumeDelay: Int = 100
    var consumeAmount: Int = 10
    var futureDelay: Int = 10
    var helperThreads: Int = 2

    fun setAndPrintConsumeDelay(value: Int) {
        if (value != consumeDelay) {
            consumeDelay = value
            println("Set consume delay to $value.")
        }
    }

    fun setAndPrintConsumeAmount(value: Int) {
        if (value != consumeAmount) {
            consumeAmount = value
            println("Set consume amount to $value.")
        }
    }

    fun setAndPrintFutureDelay(value: Int) {
        if (value != futureDelay) {
            futureDelay = value
            println("Set print delay to $value.")
        }
    }

    fun setAndPrintHelperThreads(value: Int) {
        if (value != helperThreads) {
            helperThreads = value
            println("Set helper threads to $value.")
        }
    }
}
