package location

import catalog.Project
import location.GradleBuildParser.Companion.gradleAstParser
import utils.Serialization.fromJson
import utils.Serialization.writeTo
import java.nio.file.Path

internal data class UserHome(override val directory: Path) : Location {

  private val projectJson = gradleAstParser().resolve("current-project.json")
  var currentProject: Project
    get() = run {
      projectJson.fromJson<Project>() ?: throw IllegalArgumentException("No project defined!")
    }
    set(value) {
      value
        .writeTo(projectJson)
    }
}