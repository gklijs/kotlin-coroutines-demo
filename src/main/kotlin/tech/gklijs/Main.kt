import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import tech.gklijs.ActionType
import tech.gklijs.RunType
import tech.gklijs.consumer.ConcurrentProducer
import tech.gklijs.util.Delay
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin coroutines demo")
    val delay by parser.option(ArgType.Int, shortName = "d", description = "delay for each action in seconds")
        .default(1)
    val times by parser.option(ArgType.Int, shortName = "t", description = "times to run an action").default(10)
    val actionType by parser.option(ArgType.Choice<ActionType>(), shortName = "a", description = "action to execute")
        .default(ActionType.DELAYED)
    val runType by parser.option(ArgType.Choice<RunType>(), shortName = "r", description = "how to run the actions")
        .default(RunType.CD)

    try {
        parser.parse(args)
        println("Will start running action $actionType with a delay of $delay seconds for a total of $times times, run as $runType")

        val startTime = Instant.now()
        runType.run.invoke(delay, times, actionType)
        val endTime = Instant.now()

        println("Running the actions took approximately: ${Duration.between(startTime, endTime)}")
    } finally {
        Delay.stop()
        ConcurrentProducer.stop()
    }
}