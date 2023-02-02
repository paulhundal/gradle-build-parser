package commands

import options.CommonOptions
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Mixin
import utils.BuildFilesLocation
import utils.FileWriter
import utils.FindProjectFiles
import java.util.concurrent.Callable

@Command(
  name = "list-all",
  version = ["1.0"],
  description = ["List all build.gradle files in project."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class ListBuildFilesCommand(
  private val logger: Logger,
  private val findProjectFiles: FindProjectFiles,
  private val fileWriter: FileWriter,
  private val buildFilesLocation: BuildFilesLocation
) : Callable<Int> {

  @Mixin
  val common = CommonOptions()

  override fun call(): Int {
    if (common.repository == null) {
      logger.error("Set --repository-path to the repository to list build files for.")
      return 1
    }
    logger.info("Listing all build.gradle files in ${common.repository}")
    val buildFiles = findProjectFiles.getAll(common.repository!!)
    val out = buildFilesLocation.get()

    if (out == null) {
      logger.error("Something went wrong while trying to create a list of build files. " +
        "Please delete ~HOME/gradle-ast-parser directory and try again.")
      return 1
    }
    val stringify = buildFiles.joinToString(separator = "\n") { it.toString() }
    fileWriter.write(stringify, out)
    return 0
  }
}