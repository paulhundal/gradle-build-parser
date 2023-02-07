package commands

import converter.IgnoreListConverter
import converter.RepositoryConverter
import location.GradleBuildParser.Companion.gradleAstParser
import location.UserHome
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option
import utils.FindProjectFiles
import utils.StringFileWriter
import utils.exists
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.io.path.createDirectories

@Command(
  name = "build-files",
  version = ["1.0"],
  description = ["Save all build.gradle files in project."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class SaveBuildFilesCommand(
  private val logger: Logger,
  private val findProjectFiles: FindProjectFiles,
  private val fileWriter: StringFileWriter,
  private val userHome: UserHome
) : Callable<Int> {

  @Option(
    names = ["--repository-path"],
    required = true,
    converter = [RepositoryConverter::class],
    description = [
      "Define for which repository to parse AST data."
    ]
  )
  lateinit var repository: Path

  @Option(
    names = ["--ignore-list"],
    converter = [IgnoreListConverter::class],
    defaultValue = "",
    description = [
      "Define which build.gradle files to ignore. Takes in a .txt file of a list of all paths."
    ]
  )
  lateinit var ignoredList: Set<Path>

  override fun call(): Int {
    logger.info("Saving build.gradle file paths found in $repository")
    ignoredList.forEach { logger.info("Ignoring $it") }
    val buildFiles = findProjectFiles.getAll(repository, ignoredList)

    val gradleAstParser = userHome.gradleAstParser()

    // Create all necessary preliminary directories
    if(!gradleAstParser.exists()) {
      gradleAstParser.createDirectories()
    }
    val root = gradleAstParser.resolve(repository.fileName.toString())
    if (!root.exists()) {
      root.createDirectories()
    }

    // Save all build files in the root under the node of the repo for which the build
    // files are being evaluated.
    if (buildFiles.isNotEmpty()) {
      val stringify = buildFiles.joinToString(separator = "\n") { it.toString() }
      val out = createOrOverwriteFile(root.resolve("build-files-list.txt").toString())
      if (out != null) {
        fileWriter.write(stringify, out)
      }
    }
    return 0
  }

  private fun createOrOverwriteFile(fileName: String): Path? {
    return try {
      val file = File(fileName)
      if (file.exists()) file.delete()
      file.createNewFile()
      return file.toPath()
    } catch (ex: Exception) {
      logger.error("Could not create $fileName", ex)
      null
    }
  }
}