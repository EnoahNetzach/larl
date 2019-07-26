package org.larl

import org.larl.printer.*

fun main() {
    val inputString = run {
        val nLastEmptyStrings = 2

        val inputLines = mutableListOf<String>()
        println("Models:")
        while (inputLines.takeLastWhile { line -> line == "" }.size < nLastEmptyStrings) {
            inputLines += readLine()!!
        }

        inputLines
            .dropLast(nLastEmptyStrings)
            .joinToString("\n")
    }

//    val inputString = """
//        module /com/test/Test
//
//        Test
//          bool isEnabled
//          str name {"Pippo"}
//          array<str>[3] address {("asdf", "qert", "erty")}
//          vec<str> names {("a", "b", "c", "d")}
//          vec<array<array<str>[2]>[3]> cmplx {((("a", "b"), ("c", "d"), ("e", "f")), ("g", "h"), ("i", "j"), ("k", "l"))}
//          tuple<str, str, bool, i32> data {("asdf", "qwer", true, 42)}
//    """.trimIndent()

    val extractor = SourceExtractor()
    extractor.extract(inputString)

    println(extractor.modules.joinToString("\n\n"))

    val javaPrinter = JavaPrinter("./")
    val kotlinPrinter = KotlinPrinter("./")
    val rustPrinter = RustPrinter("./")
    val javaScriptPrinter = JavaScriptPrinter("./")
    val typeScriptPrinter = TypeScriptPrinter("./")
    val flowPrinter = FlowPrinter("./")
    val goPrinter = GoPrinter("./")

    javaPrinter.print(extractor.modules)
    kotlinPrinter.print(extractor.modules)
    rustPrinter.print(extractor.modules)
    javaScriptPrinter.print(extractor.modules)
    typeScriptPrinter.print(extractor.modules)
    flowPrinter.print(extractor.modules)
    goPrinter.print(extractor.modules)
}
