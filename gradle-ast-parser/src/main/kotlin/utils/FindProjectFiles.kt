package utils

import java.nio.file.Path
import java.util.LinkedList

internal interface FindProjectFiles {
  /**
   * Returns all build files in this project
   *
   * @param from The root project to search from
   */
  fun getAll(from: Path, ignore: Set<Path>): Set<Path>
}

internal class DefaultFindProjectFiles : FindProjectFiles {
  /**
   * BFS approach to gathering all build.gradle files in a project directory.
   */
  override fun getAll(from: Path, ignore: Set<Path>): Set<Path> {
    val gradleFiles = mutableSetOf<Path>()
    val queue: LinkedList<Path> = LinkedList()
    queue.add(from)

    while (queue.isNotEmpty()) {
      val currentDirectory = queue.poll().toFile()
      currentDirectory.listFiles()?.forEach { file ->
        if (file.isFile && file.name == BUILD_GRADLE_FILE && !ignore.contains(file.toPath())) {
          gradleFiles.add(file.toPath())
        } else if (file.isDirectory) {
          queue.add(file.toPath())
        }
      }
    }
    
    return gradleFiles
  }

  companion object {
    const val BUILD_GRADLE_FILE = "build.gradle"
  }

}