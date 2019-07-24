package org.larl.entities

import larl.grammar.LarlLexer

enum class Types(val identifier: String) {
    BOOL(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.BOOL_T).trim('\'')),
    CHAR(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.CHAR_T).trim('\'')),
    I8(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.I8_T).trim('\'')),
    I16(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.I16_T).trim('\'')),
    I32(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.I32_T).trim('\'')),
    I64(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.I64_T).trim('\'')),
    ISIZE(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.ISIZE_T).trim('\'')),
    U8(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.U8_T).trim('\'')),
    U16(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.U16_T).trim('\'')),
    U32(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.U32_T).trim('\'')),
    U64(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.U64_T).trim('\'')),
    USIZE(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.USIZE_T).trim('\'')),
    F32(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.F32_T).trim('\'')),
    F64(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.F64_T).trim('\'')),
    ARRAY(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.ARRAY_T).trim('\'')),
    VECTOR(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.VECTOR_T).trim('\'')),
    STR(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.STR_T).trim('\'')),
    TUPLE(LarlLexer.VOCABULARY.getLiteralName(LarlLexer.TUPLE_T).trim('\''));

    companion object {
        private val typeMap = run {
            values().map { it.identifier to it }.toMap()
        }

        fun get(identifier: String) = typeMap.getOrDefault(identifier, null)
    }
}
