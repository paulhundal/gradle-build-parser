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
import location.GlobalScope
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option
import utils.Configuration
import utils.Status.VALID
import java.util.concurrent.Callable

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
  private val globalScope: GlobalScope,
  private val setupConfiguration: Configuration<Project, GlobalScope>
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
    logger.info("Setting up project configuration")
    globalScope.userHome.currentProject = project
    if (setupConfiguration.applyFor(project, globalScope) == VALID) {
      return 0
    }
    return 1
  }
}