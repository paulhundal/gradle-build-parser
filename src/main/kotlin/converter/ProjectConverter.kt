package converter

import catalog.ProjectCatalog
import catalog.Project
import picocli.CommandLine.ITypeConverter

internal class ProjectConverter(
  private val projectCatalog: ProjectCatalog
) : ITypeConverter<Project> {
  override fun convert(value: String): Project {
    return projectCatalog.find(value)
  }
}