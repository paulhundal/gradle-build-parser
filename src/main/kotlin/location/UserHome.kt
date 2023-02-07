package location

import catalog.Project
import location.GradleBuildParser.Companion.gradleAstParser
import utils.Serialization.fromJson
import utils.Serialization.writeTo
import java.nio.file.Path

internal data class UserHome(override val directory: Path) : Location {

  private val allProjects = gradleAstParser().resolve("projects-all.json")
  private val projectJson = gradleAstParser().resolve("current-project.json")
  var currentProject: Project
    get() = run {
      projectJson.fromJson<Project>() ?: throw IllegalArgumentException("No project defined!")
    }
    set(value) {
      value
        .ensure()
        .apply { updateALlProjects(this) }
        .writeTo(projectJson)
    }

  private fun Project.ensure(): Project = when {
    name.isBlank() -> copy(name = "ast-parser")
    else -> this
  }

  fun allProjects(): Set<Project> {
    return allProjects.fromJson<Set<Project>>()
      ?.mapTo(HashSet()) { it.ensure() }
      ?: projectJson.fromJson<Project>()?.ensure()?.let { setOf(it) }
      ?: emptySet()
  }

  private fun updateALlProjects(project: Project) {
    val projects = allProjects().toMutableList()
    projects += project
    projects.writeTo(allProjects)
  }
}