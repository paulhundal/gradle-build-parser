import commands.AstCommand
import di.PicoFactory
import org.slf4j.LoggerFactory
import picocli.CommandLine
import utils.ProcessExecute

internal val LOGGER = LoggerFactory.getLogger("ast-parser")

fun main(args: Array<String>) {
  ProcessExecute(LOGGER).use {
    val picoCli = PicoFactory()
    val cli = CommandLine(AstCommand(), picoCli)
    cli.execute(*args)
  }
}