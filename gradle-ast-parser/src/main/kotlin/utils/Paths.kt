package utils

import java.nio.file.Files
import java.nio.file.Path

/**
 * Given a module target like `:common:development-app:demo-tictactoe`, converts it into a canonical
 * path rooted on the Register repo: `/path/to/android-register/common/development-app/demo-tictactoe`
 */
fun canonicalPath(
  root: Path,
  target: String
): Path {
  return root.resolve(target.replace(':', '/').removePrefix("/"))
}

/**
 * Returns the last path element as a string. E.g., `common/development-app/demo-tictactoe` becomes
 * `demo-tictactoe`.
 */
fun Path.filePath() = fileName.toString()

/** True if the [File][java.io.File] pointed to by this [Path] exists. */
fun Path.exists() = Files.exists(this)

/** Writes [text] to given `Path`. */
fun Path.writeText(text: String) {
  Files.write(this, text.toByteArray())
}

/**
 * True if the sibling given by [path] has a `build.gradle` file. I.e., if that sibling is a Gradle
 * module.
 */
fun Path.buildFileExistsForSibling(path: String): Boolean {
  return resolveSibling(path).resolve("build.gradle").exists()
}