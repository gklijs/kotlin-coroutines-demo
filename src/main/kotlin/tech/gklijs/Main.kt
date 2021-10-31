import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import tech.gklijs.ActionType
import tech.gklijs.Constants
import tech.gklijs.RunType
import tech.gklijs.consumer.ConcurrentProducer
import tech.gklijs.util.Delay
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin coroutines demo")
    val delay by parser.option(ArgType.Int, shortName = "d", description = "delay for each action in milliseconds")
        .default(1000)
    val times by parser.option(ArgType.Int, shortName = "t", description = "times to run an action").default(10)
    val actionType by parser.option(ArgType.Choice<ActionType>(), shortName = "a", description = "action to execute")
        .default(ActionType.DELAYED)
    val runType by parser.option(ArgType.Choice<RunType>(), shortName = "r", description = "how to run the actions")
        .default(RunType.SAME_THREAD)
    val consumeDelay by parser.option(ArgType.Int,
        shortName = "cd",
        description = "max delay between poll calls in milliseconds").default(100)
    val consumeAmount by parser.option(ArgType.Int,
        shortName = "ca",
        description = "amount of items to consume before closing the consumer").default(10)
    val futureDelay by parser.option(ArgType.Int,
        shortName = "fd",
        description = "amount of time between checking if the future has resolved").default(10)

    try {
        parser.parse(args)
        Constants.setAndPrintConsumeDelay(consumeDelay)
        Constants.setAndPrintConsumeAmount(consumeAmount)
        Constants.setAndPrintFutureDelay(futureDelay)
        println("Will start ${actionType.description} with a delay of $delay milliseconds for a total of $times times, ${runType.description}.")

        val startTime = Instant.now()
        runType.run.invoke(delay, times, actionType)
        val endTime = Instant.now()

        println("Running the actions took approximately: ${Duration.between(startTime, endTime)}")
    } finally {
        Delay.stop()
        ConcurrentProducer.stop()
    }
}