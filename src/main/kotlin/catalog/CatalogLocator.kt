package catalog

import utils.Distribution

internal class CatalogLocator(private val distribution: Distribution) {
  fun findProject() = ProjectCatalog.newInstance(distribution.findFile("project-catalog.json"))
}