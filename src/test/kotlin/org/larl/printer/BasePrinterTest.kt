package org.larl.printer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.larl.BaseMockingTest
import org.mockito.Mockito

internal class BasePrinterTest : BaseMockingTest {
    private lateinit var printer: BasePrinter

    @BeforeEach
    fun setUp() {
        printer = Mockito.mock(BasePrinter::class.java)
        Mockito.doCallRealMethod().`when`(printer).splitCollection(Mockito.anyString())
    }

    @Test
    fun `should correctly split a simple collection`() {
        assertEquals(listOf("\"asdf\"", "\"qwer\"", "true", "42"), printer.splitCollection("(\"asdf\", \"qwer\", true, 42)"))
    }

    @Test
    fun `should correctly split a nested collection`() {
        assertEquals(listOf("(\"asdf\", \"qwer\")", "(true, 42)"), printer.splitCollection("((\"asdf\", \"qwer\"), (true, 42))"))
    }

    @Test
    fun `should correctly split a deeply nested collection`() {
        assertEquals(listOf("(1, (2, 3))", "(((4, 5, 6), 7, (8, 9)))", "0"), printer.splitCollection("((1, (2, 3)), (((4, 5, 6), 7, (8, 9))), 0)"))
    }
}
