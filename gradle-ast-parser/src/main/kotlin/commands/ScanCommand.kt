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

import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import java.util.concurrent.Callable

@Command(
  name = "scan",
  description = ["Scan the current project configuration and display to user."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class ScanCommand(
  private val globalScope: GlobalScope,
  private val logger: Logger
) : Callable<Int> {

  override fun call(): Int {
    logger.info("Displaying project configuration below: \n")
    val project = globalScope.userHome.currentProject
    logger.info("Current project: ${project.name}")
    logger.info("Current project location: ${globalScope.userHome.directory}/${project.path}")
    logger.info(
      "Current project violations location: ${
        globalScope.userHome.gradleAstParser().resolve(project.path)
      }"
    )
    return 0
  }
}