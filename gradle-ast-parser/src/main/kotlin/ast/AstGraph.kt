package ast

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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.slf4j.Logger
import java.nio.file.Path
import java.util.TreeMap
import kotlin.io.path.readText

internal interface AstGraph {
  fun walk(buildFiles: Set<Path>): Map<Path, List<ASTNode>>
}

internal class DefaultAstGraph(
  private val logger: Logger,
  private val astBuilder: AstBuilder
) : AstGraph {
  private val astMap: TreeMap<Path, List<ASTNode>> = TreeMap()
  override fun walk(buildFiles: Set<Path>): Map<Path, List<ASTNode>> {
    buildFiles.forEach { file ->
      try {
        val nodes = astBuilder.buildFromString(file)
        astMap[file] = nodes
      } catch (ex: Exception) {
        logger.error("Could not parse AST nodes from $file")
      }
    }
    return astMap.toMap()
  }

  private fun AstBuilder.buildFromString(buildFile: Path): List<ASTNode> {
    return try {
      buildFromString(buildFile.readText())
    } catch (ex: Exception) {
      logger.warn("Unable to parse AST of build file $buildFile, skipping")
      emptyList()
    }
  }
}

