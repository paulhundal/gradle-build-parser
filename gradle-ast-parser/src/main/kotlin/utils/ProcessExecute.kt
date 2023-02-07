package utils

import okio.buffer
import okio.source
import org.slf4j.Logger
import java.io.InputStream
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS

/** [ProcessExecute] implements [Execute] using [ProcessBuilder]. */
class ProcessExecute(
  private val logger: Logger,
  private val executor: ExecutorService = Executors.newCachedThreadPool()
) : Execute, AutoCloseable {
  override fun stream(
    vararg commands: String,
    workingDirectory: Path,
    sink: (err: CharSequence?, out: CharSequence?) -> Unit
  ): Int {
    ProcessBuilder().apply {
      environment()["PROMPT"] = "$" // disable user prompt.
      command(*commands)
      directory(workingDirectory.toFile())
    }.start().run {
      executor
        .invokeAll(
          listOf(
            inputStream.consumeLines { line -> sink(null, line) },
            errorStream.consumeLines { line -> sink(line, null) }
          )
        )
        .forEach { it.get() }

      return waitFor()
    }
  }

  private fun InputStream.consumeLines(accept: (String) -> Unit): Callable<Unit> = Callable {
    val stdErr = source().buffer()
    while (true) {
      val line = stdErr.readUtf8Line() ?: break
      accept(line)
    }
  }

  override fun close() {
    executor.shutdown()
    if (!executor.awaitTermination(10, SECONDS)) {
      logger.warn("Hanging tasks post execution: ${executor.shutdownNow()}")
    }
  }
}
