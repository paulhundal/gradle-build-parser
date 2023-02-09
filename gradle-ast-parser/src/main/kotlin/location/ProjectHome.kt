package location

import location.GradleBuildParser.Companion.gradleAstParser
import java.nio.file.Path

internal class ProjectHome(override val directory: Path) : Location {
  companion object {
    fun UserHome.projectHome(projectName: String) = gradleAstParser().resolve(projectName)
  }
}