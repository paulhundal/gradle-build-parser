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

import ast.rule.ClosureRule
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.slf4j.Logger

internal class ClosureVisitor(
  closureRules: List<ClosureRule>,
  private val logger: Logger,
  private val allowlistClosures: Set<String>,
  visitorManager: VisitorManager
) : Visitor(closureRules, visitorManager) {

  private val methodCalls: HashMap<Int, String> = HashMap()
  private var start = -1
  private var end = -1

  // Maintain a blocked out list of line numbers that we don't visit if they're within a closure
  // that is nested and that we ignore
  private val ignoredBlocks: ArrayList<Int> = ArrayList()

  override fun visitMethodCallExpression(call: MethodCallExpression) {
    if (allowlistClosures.contains(call.methodAsString)) {
      start = call.lineNumber
      end = call.lastLineNumber
      for (x in start + 1..end) {
        ignoredBlocks.add(x)
      }
    }
    val method = call.method.text
    methodCalls[call.method.lineNumber] = method
    super.visitMethodCallExpression(call)
  }

  override fun visitClosureExpression(expression: ClosureExpression) {
    val lineNumber = expression.lineNumber
    // We don't care about closures within ignored blocks
    if (methodCalls.containsKey(lineNumber) && !ignoredBlocks.contains(lineNumber)) {
      val name = methodCalls.getValue(lineNumber)
      add(name, expression.lineNumber)
    }
    super.visitClosureExpression(expression)
  }

  override fun clear() {
    methodCalls.clear()
    ignoredBlocks.clear()
    start = -1
    end = -1
  }
}

