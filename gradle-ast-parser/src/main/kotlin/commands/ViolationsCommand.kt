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

import ast.violation.IncludedViolation
import ast.violation.IncludedViolation.DuplicateClosures
import ast.violation.IncludedViolation.UnsupportedClosures
import ast.visitor.Visitor
import ast.visitor.VisitorFactory
import location.GlobalScope
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Parameters
import utils.Configuration
import utils.Status.VALID
import java.util.concurrent.Callable

@Command(
  name = "violations",
  version = ["1.0"],
  description = ["Find violations in the AST of the current project."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class ViolationsCommand(
  private val logger: Logger,
  private val globalScope: GlobalScope,
  private val visitorFactory: VisitorFactory,
  private val violationsConfiguration: Configuration<List<Visitor>, GlobalScope>
) : Callable<Int> {

  @Parameters(
    paramLabel = "VIOLATION",
    description = ["one or more violations to find."]
  )
  var includeViolations: Array<IncludedViolation> = emptyArray()

  override fun call(): Int {
    if (includeViolations.isEmpty()) {
      logger.info(
        "By not passing any parameters for VIOLATION this program will check " +
          "all violations that are supported. If this wasn't the intention please see the" +
          "complete list of violation checks by running 'violations --help'"
      )
      includeViolations.toMutableList().apply {
        add(UnsupportedClosures)
        add(DuplicateClosures)
      }
    }
    // Setup visitors that will visit each node of the AST
    val visitors = visitorFactory.create(includeViolations.toList())
    if (violationsConfiguration.applyFor(visitors, globalScope) == VALID) {
      return 0
    }

    return 1
  }
}