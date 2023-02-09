package ast

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

import kotlin.collections.Map.Entry

internal data class Dependency(
  val type: String,
  val declaration: String,
  val lineNumber: Int
) {
  override fun toString(): String {
    return "$type($declaration)"
  }

  fun isTest(): Boolean = type.lowercase().contains("test")

  fun isCompileOnly(): Boolean = type.endsWith("compileOnly", ignoreCase = true)

  fun isProd(): Boolean {
    return !isTest()
  }

}

/**
 * Converts a map entry of dependencies from a string to a [Dependency]
 * for convenience of parsing individual parts of the dependency block.
 */
internal fun Entry<String, List<Int>>.toDependency(): List<Dependency> {
  val regex = Regex("(.*)\\((.*)\\)")
  return this.value.mapNotNull { lineNumber ->
    val matchResult = regex.matchEntire(this.key)
    if (matchResult != null) {
      val (type, declaration) = matchResult.destructured
      Dependency(type.trim()
        .replace("(", " ")
        .replace("project", "")
        .replace("platform", "")
        .trim(),
        declaration.trim()
          .replace(")", ""),
        lineNumber
      )
    } else {
      null
    }
  }
}
