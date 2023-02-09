package configuration

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
import converter.RepositoryConverter
import location.Binary.Companion.binary
import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.slf4j.Logger
import utils.BUILD_FILES
import utils.Configuration
import utils.Files
import utils.FindProjectFiles
import utils.IGNORE_BUILDS
import utils.Status
import utils.Status.ERROR
import utils.Status.VALID
import utils.StringFileWriter
import utils.exists
import kotlin.io.path.createDirectories
import kotlin.io.path.readLines

internal class SetupConfiguration(
  private val findProjectFiles: FindProjectFiles,
  private val fileWriter: StringFileWriter,
  private val files: Files,
  private val repositoryConverter: RepositoryConverter,
  private val logger: Logger
) : Configuration<Project, GlobalScope> {

  override fun applyFor(chosen: Project, to: GlobalScope): Status {
    try {
      val ignoreBuilds = to.binary().resolve(IGNORE_BUILDS).readLines().map {
        to.fileSystem.getPath(it)
      }.toSet()

      val projectPath = repositoryConverter.convert(chosen.path)
        ?: to.fileSystem.getPath(chosen.path)

      logger.info("Saving build.gradle file paths for project ${chosen.name}")
      val buildFiles = findProjectFiles.getAll(projectPath, ignoreBuilds)

      val gradleAstParser = to.userHome.gradleAstParser()

      // Create all necessary preliminary directories
      if (!gradleAstParser.exists()) {
        gradleAstParser.createDirectories()
      }
      val root = gradleAstParser.resolve(chosen.name)
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
    } catch (ex : Exception) {
      logger.error("Error in setup configuration", ex)
      return ERROR
    }
    return VALID
  }
}