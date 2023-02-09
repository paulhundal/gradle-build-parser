package ast.visitor

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

import ast.rule.ClosureNotSupportedRule
import ast.rule.DuplicateClosureRule
import ast.rule.UnsupportedDependencyRule
import converter.UndesiredDependencyConverter
import org.slf4j.Logger

internal class VisitorFactory(
  private val allowList: Set<String>,
  private val undesiredDependencyConverter: UndesiredDependencyConverter,
  private val logger: Logger,
  private val visitorManager: VisitorManager
) {
  fun create(): List<Visitor> {
    // Each visitor can enforce a list of rules. To append a rule to a visitor append it
    // to the list here
    val closureVisitors = ClosureVisitor(
      allowlistClosures = allowList,
      logger = logger,
      closureRules = listOf(
        ClosureNotSupportedRule(allowList, logger, visitorManager),
        // DuplicateClosureRule(visitorManager, allowList)
      ),
      visitorManager = visitorManager
    )

    val dependencyVisitors = DependenciesVisitor(
      dependencyRules = listOf(
        // UnsupportedDependencyRule(
        //   logger,
        //   visitorManager,
        //   undesiredDependencyConverter
        // )
      ),
      visitorManager = visitorManager
    )

    return listOf(closureVisitors, dependencyVisitors)
  }
}