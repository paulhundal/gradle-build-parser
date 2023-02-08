package ast

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
internal fun Entry<String, Int>.toDependencyOrNull(): Dependency? {
  val regex = Regex("(.*)\\((.*)\\)")
  val matchResult = regex.matchEntire(this.key)
  return if (matchResult != null) {
    val (type, declaration) = matchResult.destructured
    Dependency(type.trim()
      .replace("(", " ")
      .replace("project", "")
      .replace("platform", "")
      .trim(),
      declaration.trim()
        .replace(")", ""),
      this.value
    )
  } else {
    null
  }
}