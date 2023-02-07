package converter

import org.slf4j.Logger
import picocli.CommandLine.ITypeConverter
import utils.DistributionProvider
import java.nio.file.Paths

internal class AllowlistConverter(
  private val logger: Logger,
  private val distributionProvider: DistributionProvider
) : ITypeConverter<Set<String>> {
  override fun convert(value: String): Set<String> {
    return try {
      return distributionProvider.getList(Paths.get(value).toString())
    } catch (ex: Exception) {
      logger.error("Unable to read allowlist catalog",ex)
      emptySet()
    }
  }
}