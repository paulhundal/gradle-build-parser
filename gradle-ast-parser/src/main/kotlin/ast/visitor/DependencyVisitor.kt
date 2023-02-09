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

import ast.rule.DependencyRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Visits dependency block and returns all dependency statements without the reflection statement
 * (e.g. this.) block.
 */
internal class DependenciesVisitor(
  dependencyRules: List<DependencyRule>,
  visitorManager: VisitorManager
): Visitor(dependencyRules, visitorManager) {

  private var depsStart = -1
  private var depsEnd = -1

  override fun visitMethodCallExpression(call: MethodCallExpression) {
    if (call.methodAsString == "dependencies") {
      depsStart = call.lineNumber
      depsEnd = call.lastLineNumber
    }
    super.visitMethodCallExpression(call)
  }

  override fun visitExpressionStatement(statement: ExpressionStatement) {
    if (statement.lineNumber > depsStart && statement.lastLineNumber < depsEnd) {
      add(statement.text.replace("this.", ""), statement.lineNumber)
    }
    super.visitExpressionStatement(statement)
  }

  override fun clear() {
    depsStart = -1
    depsEnd = -1
  }
}
