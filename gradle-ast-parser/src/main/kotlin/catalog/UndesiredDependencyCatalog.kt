package catalog

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

import kotlinx.serialization.Serializable
import utils.Serialization.fromJson
import java.nio.file.Path
import kotlin.io.path.readText

@Serializable
internal class UndesiredDependencyCatalog(private val undesired: Map<String, UndesiredDependency>) {

  fun find(name: String): UndesiredDependency {
    return undesired[name]
      ?: undesired.values.firstOrNull { deps -> deps.name == name }
      ?: throw IllegalArgumentException("No undesirable dependency found with name $name")
  }

  companion object {
    fun of(file: Path): UndesiredDependencyCatalog = file.readText().fromJson()
  }
}

@Serializable
internal data class UndesiredDependency(
  val name: String,
  val declaration: String,
  val reason: String
)
