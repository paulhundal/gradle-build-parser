package utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

object Serialization {
  inline fun <reified T> String.fromJson(): T = Json.decodeFromString(this)
  inline fun <reified T> T.toJson(): String = Json.encodeToString(this)

  inline fun <reified T> Path.fromJson(): T? {
    if (!Files.exists(this)) return null

    return runCatching {
      readText().fromJson<T>()
    }.getOrNull()
  }

  inline fun <reified T> T.writeTo(path: Path) {
    path.writeText(toJson())
  }
}