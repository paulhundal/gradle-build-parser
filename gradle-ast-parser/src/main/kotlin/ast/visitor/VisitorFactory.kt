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
import ast.rule.ClosureRule
import ast.rule.DuplicateClosureRule
import ast.violation.IncludedViolation
import ast.violation.IncludedViolation.DuplicateClosures
import ast.violation.IncludedViolation.UnsupportedClosures
import org.slf4j.Logger

internal class VisitorFactory(
  private val allowList: Set<String>,
  private val logger: Logger,
  private val visitorManager: VisitorManager
) {

  fun create(includedViolation: List<IncludedViolation>): List<Visitor> {
    val closureRules: MutableList<ClosureRule> = mutableListOf()
    includedViolation.forEach { violation ->
      when (violation) {
        UnsupportedClosures -> {
          closureRules.add(ClosureNotSupportedRule(allowList, logger, visitorManager))
        }
        DuplicateClosures -> {
          closureRules.add(DuplicateClosureRule(visitorManager, allowList))
        }
      }
    }

    val closureVisitors = ClosureVisitor(
      allowlistClosures = allowList,
      logger = logger,
      closureRules = closureRules.toList(),
      visitorManager = visitorManager
    )

    return listOf(closureVisitors)
  }
}