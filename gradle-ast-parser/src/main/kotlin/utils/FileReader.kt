package utils

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines

internal interface FileReader<TYPE> {
  fun read(from: Path): TYPE
}

internal class StringPathFileReader : FileReader<Set<Path>> {
  override fun read(from: Path): Set<Path> {
    return from.readLines().map { Paths.get(it) }.toSet()
  }
}