package location

import java.nio.file.Path

internal data class GradleBuildParser(override val directory: Path) : Location {
  companion object {
    fun UserHome.gradleAstParser() = resolve(".ast")
  }
}