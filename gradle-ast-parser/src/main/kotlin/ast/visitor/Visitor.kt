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

import ast.rule.Rule
import org.codehaus.groovy.ast.CodeVisitorSupport

/**
 * Marker interface to identify a Visitor of the Groovy AST
 */
internal abstract class Visitor(
  val rules: List<Rule>,
  private val visitorManager: VisitorManager
) : CodeVisitorSupport() {

  // val visitedNodes: MutableMap<Class<out Visitor>, VisitedNodes> =
  //   visitorManager.getVisitedNodes().toMutableMap()

  fun add(
    node: String,
    position: Int
  ) = visitorManager.add(this.javaClass, node, position)

  abstract fun clear()
}
