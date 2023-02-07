package utils

import java.nio.file.Path

/** [Execute] standardizes calling arbitrary binaries with arguments. */
fun interface Execute {

  class InDirectoryExecute(
    private val directory: Path,
    private val execute: Execute
  ) {
    fun call(
      binary: Path,
      vararg arguments: String
    ): Result {
      return execute.call(binary, *arguments, workingDirectory = directory)
    }

    fun stream(
      vararg commands: String,
      sink: (err: CharSequence?, out: CharSequence?) -> Unit
    ): Int = execute.stream(*commands, workingDirectory = directory, sink = sink)
  }

  fun <T> inDirectory(
    directory: Path,
    actions: InDirectoryExecute.() -> T
  ): T {
    return InDirectoryExecute(directory, this).run(actions)
  }

  /** [call] a [binary] and return a [Result]. */
  fun call(
    binary: Path,
    vararg arguments: String,
    workingDirectory: Path
  ) = call(binary.toString(), *arguments, workingDirectory = workingDirectory)

  /** [call] a [binary] and return a [Result]. */
  fun call(
    binary: String,
    vararg arguments: String,
    workingDirectory: Path
  ): Result {
    val commands = (arrayOf(binary) + arguments)
    val outBuffer = mutableListOf<CharSequence>()
    val errBuffer = mutableListOf<CharSequence>()
    val exit = stream(*commands, workingDirectory = workingDirectory) { err, out ->
      out?.run(outBuffer::add)
      err?.run(errBuffer::add)
    }
    return Result(
      commands.joinToString(" "),
      exit,
      outBuffer.joinToString("\n"),
      errBuffer.joinToString("\n")
    )
  }

  class ExecuteException(result: Result) : RuntimeException(result.toString())

  /** [Result] of a process execution. */
  data class Result(
    val command: String,
    val exit: Int,
    val out: CharSequence,
    val err: CharSequence
  ) {
    /** Throw an [ExecuteException] if [exit] is not 0 */
    fun onErrorThrow(): Result {
      if (exit != 0) {
        throw ExecuteException(this)
      }
      return this
    }
  }

  /** [stream] the [commands] executed in [workingDirectory] to [sink] and return an exit  code. */
  fun stream(
    vararg commands: String,
    workingDirectory: Path,
    sink: (err: CharSequence?, out: CharSequence?) -> Unit
  ): Int
}
