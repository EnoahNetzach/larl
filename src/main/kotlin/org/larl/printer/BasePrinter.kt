package org.larl.printer

import org.larl.entities.Field
import org.larl.entities.Model
import org.larl.entities.Module
import org.larl.entities.Type
import java.io.File

abstract class BasePrinter(protected val basePath: String) {
    init {
        mkdirp(basePath)
    }

    abstract fun print(modules: List<Module>)

    internal abstract fun handleModule(module: Module): String

    internal abstract fun handleModel(model: Model): String

    internal abstract fun handleField(field: Field): String

    internal open fun handleType(type: Type): String = type.identifier

    internal open fun handleDefault(field: Field) = field.default ?: ""

    internal fun mkdirp(path: String) = mkdirp(File(path))

    internal fun mkdirp(file: File) = assert(file.mkdirs()) { "Failed to create path ${file.path}" }

    internal fun splitCollection(default: String?): List<String> = if (default == null) emptyList() else {
        var nesting = 0

        val content = default.drop(1).dropLast(1)

        val acc = mutableListOf("")
        var accIndex = 0
        for (char in content) {
            when (char) {
                '(' -> {
                    nesting++
                    acc[accIndex] = acc[accIndex].plus(char)
                }
                ')' -> {
                    nesting--
                    acc[accIndex] = acc[accIndex].plus(char)
                }
                ',' -> if (nesting == 0) {
                    accIndex++
                    acc += ""
                } else {
                    acc[accIndex] = acc[accIndex].plus(char)
                }
                else -> {
                    acc[accIndex] = acc[accIndex].plus(char)
                }
            }
        }

        acc.map(String::trim)
    }

    internal fun trimEmptyLines(text: String): String =
        text.split("\n").joinToString("\n") { line -> if (line.matches(Regex("^\\s+$"))) "" else line }
}
