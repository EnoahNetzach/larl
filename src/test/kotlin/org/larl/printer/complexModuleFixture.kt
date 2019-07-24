package org.larl.printer

import org.larl.entities.Field
import org.larl.entities.Model
import org.larl.entities.Module
import org.larl.entities.Type

val complexModuleFixture = Module(
    "/com/test/Test",
    listOf(
        Model(
            "Test",
            listOf(
                Field("isEnabled", Type("bool")),
                Field("name", Type("str"), "\"Pippo\""),
                Field("letters", Type("vec", listOf(Type("str"))), "(\"a\", \"b\", \"c\", \"d\")"),
                Field(
                    "cmplx",
                    Type(
                        "vec",
                        listOf(
                            Type(
                                "array",
                                listOf(Type("3"), Type("array", listOf(Type("2"), Type("str"))))
                            )
                        )
                    ),
                    "(((\"a\", \"b\"), (\"c\", \"d\"), (\"e\", \"f\")), ((\"g\", \"h\"), (\"i\", \"j\"), (\"k\", \"l\")))"
                ),
                Field(
                    "data",
                    Type("tuple", listOf(Type("str"), Type("str"), Type("bool"), Type("i32"))),
                    "(\"asdf\", \"qwer\", true, 42)"
                )
            )
        )
    )
)
