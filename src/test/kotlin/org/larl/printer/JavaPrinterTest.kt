package org.larl.printer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.larl.BaseMockingTest
import org.larl.entities.Field
import org.larl.entities.Model
import org.larl.entities.Module
import org.larl.entities.Type
import org.mockito.Mockito.*

internal class JavaPrinterTest : BaseMockingTest {
    private lateinit var printer: JavaPrinter

    @BeforeEach
    fun setUp() {
        printer = mock(JavaPrinter::class.java)
        doCallRealMethod().`when`(printer).handleModule(anyObj(Module::class.java))
        doCallRealMethod().`when`(printer).handleModel(anyObj(Model::class.java))
        doCallRealMethod().`when`(printer).handleField(anyObj(Field::class.java))
        doCallRealMethod().`when`(printer).handleType(anyObj(Type::class.java))
        doCallRealMethod().`when`(printer).handleDefault(anyObj(Field::class.java))
        doCallRealMethod().`when`(printer).splitCollection(anyString())
    }

    @Test
    fun `should print the expected module`() {
        val actual = printer.handleModule(complexModuleFixture)
        val expected = this::class.java.getResource("/org/larl/printer/JavaPrinterTest/expectedModule.txt").readText()

        assertEquals(expected, actual)
    }
}
