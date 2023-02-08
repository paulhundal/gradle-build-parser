package converter

import picocli.CommandLine.ITypeConverter
import utils.DistributionProvider

internal class DisallowedDependenciesConverter(
  private val distributionProvider: DistributionProvider
) : ITypeConverter<Set<String>>{
  override fun convert(value: String): Set<String> {
    return distributionProvider.getList(value)
  }
}