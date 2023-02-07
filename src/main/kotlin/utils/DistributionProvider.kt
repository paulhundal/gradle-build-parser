package utils

import kotlin.io.path.useLines

internal interface DistributionProvider {
  fun getList(file: String): Set<String>
}

internal class DefaultDistributionProvider(
  private val distribution: Distribution
) : DistributionProvider {
  override fun getList(file: String): Set<String> =
    distribution.findFile(file).useLines { lines ->
      return@useLines lines.filterNot { it.isEmpty() }
        .filterNot { it.startsWith("#") }
        .toSortedSet()
    }
}
