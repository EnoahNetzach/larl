package org.larl

import larl.grammar.LarlLexer
import larl.grammar.LarlParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.larl.entities.Module

class SourceExtractor {
    private val modelsExtractorListener = ModelsExtractorListener()

    val modules: List<Module>
        get() = modelsExtractorListener.modules

    fun extract(source: String) {
        val input = CharStreams.fromString(source)
        val lexer = LarlLexer(input)

        val tokens = CommonTokenStream(lexer)
        val parser = LarlParser(tokens)
        val tree = parser.modules()

        val walker = ParseTreeWalker()

        walker.walk(modelsExtractorListener, tree)
    }
}
