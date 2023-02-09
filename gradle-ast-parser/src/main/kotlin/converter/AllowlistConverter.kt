package converter

/**
 * Copyright 2022 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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