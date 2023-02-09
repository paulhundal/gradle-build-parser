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
  val path: String
)