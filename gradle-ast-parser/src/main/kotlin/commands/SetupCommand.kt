package commands

/**
 * Copyright 2022 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import catalog.Project
import converter.ProjectConverter
import converter.RepositoryConverter
import location.Binary.Companion.binary
import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option
import utils.BUILD_FILES
import utils.Files
import utils.FindProjectFiles
import utils.IGNORE_BUILDS
import utils.StringFileWriter
import utils.exists
import java.util.concurrent.Callable
import kotlin.io.path.createDirectories
import kotlin.io.path.readLines

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
  private val globalScope: GlobalScope,
  private val repositoryConverter: RepositoryConverter,
  private val files: Files
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
    globalScope.userHome.currentProject = project

    val ignoreBuilds = globalScope.binary().resolve(IGNORE_BUILDS).readLines().map {
      globalScope.fileSystem.getPath(it)
    }.toSet()

    val projectPath = repositoryConverter.convert(project.path)
      ?: globalScope.fileSystem.getPath(project.path)

    logger.info("Saving build.gradle file paths for project ${project.name}")
    val buildFiles = findProjectFiles.getAll(projectPath, ignoreBuilds)

    val gradleAstParser = globalScope.userHome.gradleAstParser()

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
      val out = files.createOrOverwriteFile(root.resolve(BUILD_FILES).toString())
      if (out != null) {
        fileWriter.write(stringify, out)
      }
    }
    return 0
  }
}