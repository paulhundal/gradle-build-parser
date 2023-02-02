package commands

import ast.AstGraph
import options.CommonOptions
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Mixin
import utils.DefaultLocation.Companion.BUILD_FILES_NAME
import utils.Location
import utils.StringPathFileReader
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(
  name = "--ast",
  version = ["1.0"],
  description = ["Build AST Graph"],
  subcommands = [
    HelpCommand::class
  ]
)
internal class BuildAstCommand(
  private val logger: Logger,
  private val astGraph: AstGraph,
  private val stringSetReader: StringPathFileReader,
  private val location: Location
) : Callable<Int> {

  @Mixin
  val common = CommonOptions()
  override fun call(): Int {
    logger.info("Building AST Graph for build files.")
    return runCatching {
      val buildFiles = getBuildFiles() ?: return 1
      val astGraph = astGraph.walk(stringSetReader.read(buildFiles))
    }.fold(
      onSuccess = {
        0
      },
      onFailure = {
        1
      }
    )
  }

  private fun getBuildFiles(): Path? {
    val buildFiles = location.get("${common.repository.toString()}-$BUILD_FILES_NAME")
    if (buildFiles == null) {
      logger.error("Something went wrong while trying to get the build files location. " +
        "Please delete ~HOME/gradle-ast-parser directory and try again.")
      return null
    }
    return buildFiles
  }
}