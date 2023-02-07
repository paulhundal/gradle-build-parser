package commands

import catalog.Project
import converter.IgnoreListConverter
import converter.ProjectConverter
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
import java.nio.file.Paths
import java.util.concurrent.Callable
import kotlin.io.path.createDirectories

@Command(
  name = "setup",
  version = ["1.0"],
  description = ["Setup project configuration for ast-parsing."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class SetupCommand(
  private val logger: Logger,
  private val findProjectFiles: FindProjectFiles,
  private val fileWriter: StringFileWriter,
  private val userHome: UserHome,
  private val repositoryConverter: RepositoryConverter,
  private val ignoreListConverter: IgnoreListConverter
) : Callable<Int> {

  @Option(
    names = ["-p", "--project"],
    defaultValue = "ast-parser",
    converter = [ProjectConverter::class],
    description = [
      "See project-catalog.txt for valid values. Defaults to '\${DEFAULT-VALUE' if not specified."
    ]
  )
  lateinit var project: Project

  override fun call(): Int {
    userHome.currentProject = project

    val projectPath = repositoryConverter.convert(project.path) ?: Paths.get(project.path)
    val ignoreBuilds = ignoreListConverter.convert(project.ignoreBuildsPathAsString)

    logger.info("Saving build.gradle file paths for project ${project.name}")
    val buildFiles = findProjectFiles.getAll(projectPath, ignoreBuilds)

    val gradleAstParser = userHome.gradleAstParser()

    // Create all necessary preliminary directories
    if(!gradleAstParser.exists()) {
      gradleAstParser.createDirectories()
    }
    val root = gradleAstParser.resolve(project.name)
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