package utils

import java.io.FileNotFoundException
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

internal interface Distribution {
  /** Find a file, which is stored in the dist root, at dist/[name]. */
  fun findFile(name: String): Path

  /** Find a nested file, located at dist/[root]/[name]. */
  fun findNestedFile(
    root: String,
    name: String
  ): Path
}

internal class RealDistribution private constructor(fs: FileSystem) : Distribution {

  private val pwd = fs.getPath(".").toAbsolutePath().normalize()
  private val distPath = "gradle-ast-parser/binary"

  override fun findFile(name: String): Path = find(name)

  override fun findNestedFile(
    root: String,
    name: String
  ): Path = find(root, name)

  private fun find(
    root: String,
    vararg others: String = emptyArray()
  ): Path {
    // This will be the path when conventions-enforcer is run from the repo root
    pwd.resolve(distPath, root, *others).also {
      if (it.exists()) return it
    }

    // Fallback
    val filename = if (others.isNotEmpty()) others.last() else root
    return Files.walk(pwd).filter {
      it.name == filename
    }.findFirst().orElseThrow {
      FileNotFoundException("Cannot find $filename")
    }
  }

  companion object {
    fun of(fs: FileSystem): Distribution = RealDistribution(fs)
  }
}

private fun Path.resolve(other: String, vararg others: String): Path {
  return others.fold(resolve(other), Path::resolve)
}
