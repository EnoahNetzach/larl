package org.larl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.larl.entities.Field
import org.larl.entities.Model
import org.larl.entities.Module
import org.larl.entities.Type

internal class SourceExtractorTest {
    private lateinit var extractor: SourceExtractor

    @BeforeEach
    fun setUp() {
        extractor = SourceExtractor()
    }

    @Test
    fun `builtin types should be extracted`() {
        extractor.extract(
            """
            module /test.me

            Test
              bool isEnabled
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(Field("isEnabled", Type("bool")))
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `array types should be extracted`() {
        extractor.extract(
            """
            module /test.me

            Test
              array<str>[3] names
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(
                                Field(
                                    "names",
                                    Type("array", listOf(Type("3"), Type("str")))
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `vector types should be extracted`() {
        extractor.extract(
            """
            module /test.me

            Test
              vec<str> names
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(
                                Field(
                                    "names",
                                    Type("vec", listOf(Type("str")))
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `tuple types should be extracted`() {
        extractor.extract(
            """
            module /test.me

            Test
              tuple<str, str> fullName
              tuple<str, i32, bool> data
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(
                                Field(
                                    "fullName",
                                    Type(
                                        "tuple",
                                        listOf(Type("str"), Type("str"))
                                    )
                                ),
                                Field(
                                    "data",
                                    Type(
                                        "tuple",
                                        listOf(
                                            Type("str"),
                                            Type("i32"),
                                            Type("bool")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `default arguments should be extracted`() {
        extractor.extract(
            """
            module /test.me

            Test
              bool isEnabled {false}
              str name {"Hello, World!"}
              array<str>[3] letters {("a", "b", "c")}
              vec<i32> ages {(1, 2, 3, 4, 5)}
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(
                                Field("isEnabled", Type("bool"), "false"),
                                Field("name", Type("str"), "\"Hello, World!\""),
                                Field("letters", Type("array", listOf(Type("3"), Type("str"))), "(\"a\", \"b\", \"c\")"),
                                Field("ages", Type("vec", listOf(Type("i32"))), "(1, 2, 3, 4, 5)")
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `the extractor should extract modules with their models`() {
        extractor.extract(
            """
            module /test.me
            
            Test
              bool isEnabled
              Foo foo
            
            Foo
              str name {"default name"}
            """.trimIndent()
        )

        assertEquals(
            extractor.modules,
            listOf(
                Module(
                    "/test.me",
                    listOf(
                        Model(
                            "Test",
                            listOf(
                                Field("isEnabled", Type("bool")),
                                Field("foo", Type("Foo"))
                            )
                        ),
                        Model(
                            "Foo",
                            listOf(Field("name", Type("str"), "\"default name\""))
                        )
                    )
                )
            )
        )
    }
}
