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

internal typealias VisitedNodes = MutableMap<String, MutableList<Int>>

/**
 * Manages the lifecycle of the visitors
 */
internal interface VisitorManager {
  fun getVisitedNodes(): Map<Class<out Visitor>, VisitedNodes>
  fun add(
    visitor: Class<out Visitor>,
    key: String,
    value: Int
  )

  fun clear()
}

internal class DefaultVisitorManager : VisitorManager {
  override fun getVisitedNodes(): Map<Class<out Visitor>, VisitedNodes> = _visitedNodes

  /**
   * Each visitor should have their own mapping to the visited nodes
   */
  private val _visitedNodes: MutableMap<Class<out Visitor>, VisitedNodes> = mutableMapOf()
  override fun add(
    visitor: Class<out Visitor>,
    key: String,
    value: Int
  ) {
    _visitedNodes.getOrPut(visitor) { mutableMapOf() }.getOrPut(key) { mutableListOf() }.add(value)
  }

  override fun clear() {
    _visitedNodes.clear()
  }
}
