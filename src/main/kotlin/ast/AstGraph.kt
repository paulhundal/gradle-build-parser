package ast

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
      logger.info(file.toString())
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

