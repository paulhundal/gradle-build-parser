package catalog

import kotlinx.serialization.Serializable
import utils.Serialization.fromJson
import java.nio.file.Path
import kotlin.io.path.readText

@Serializable
internal class ProjectCatalog(private val projects: Map<String, Project>) {

  constructor(vararg project: Pair<String, Project>) : this(project.toMap())

  fun find(name: String): Project {
    return projects[name]
      ?: projects.values.firstOrNull { project -> project.name == name }
      ?: throw IllegalArgumentException("No project found with name $name")
  }

  companion object {
    fun newInstance(file: Path): ProjectCatalog = file.readText().fromJson()
  }
}

@Serializable
data class Project(
  val name: String,
  val path: String,
  val allowlistClosuresPathAsString: String,
  val ignoreBuildsPathAsString: String
)