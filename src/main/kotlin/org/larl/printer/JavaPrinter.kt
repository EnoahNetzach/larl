package org.larl.printer

import org.larl.entities.*
import java.io.File

class JavaPrinter(basePath: String) : BasePrinter(basePath) {
    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.java")
            mkdirp(moduleFile.parentFile)
            moduleFile.writeText(handleModule(module))
        }
    }

    override fun handleModule(module: Module): String {
        val header = "package ${module.identifier.removePrefix("/").split("/").dropLast(1).joinToString(".")};"
        val imports = "import java.util.*;";
        val body = module.models.joinToString("\n\n", transform = this::handleModel)

        return "$header\n\n$imports\n\n$body\n"
    }

    override fun handleModel(model: Model): String {
        val fields = model.fields.toList().joinToString("\n\n", transform = this::handleField)

        return "final public class ${model.identifier} {\n$fields\n}"
    }

    override fun handleField(field: Field): String {
        val equalsDefault = if (field.default == null) "" else " = "
        val default = handleDefault(field)

        return "public ${handleType(field.type)} ${field.identifier}$equalsDefault$default;".prependIndent("    ")
    }

    override fun handleType(type: Type): String = when (Types.get(type.identifier)) {
        Types.BOOL -> "Boolean"
        Types.CHAR -> "Char"
        Types.I8, Types.I16, Types.I32, Types.I64, Types.ISIZE,
        Types.U8, Types.U16, Types.U32, Types.U64, Types.USIZE -> "Integer"
        Types.F32 -> "Float"
        Types.F64 -> "Double"
        Types.STR -> "String"
        Types.ARRAY -> "List<${handleType(type.subtypes[1])}>"
        Types.VECTOR -> "List<${handleType(type.subtypes[0])}>"
        Types.TUPLE -> {
            val firstType = handleType(type.subtypes[0])
            val secondType = if (type.subtypes.size == 2) {
                handleType(type.subtypes[1])
            } else {
                handleType(Type("tuple", type.subtypes.drop(1)))
            }

            "Map.Entry<$firstType, $secondType>"
        }
        else -> super.handleType(type)
    }

    override fun handleDefault(field: Field): String =
        if (field.default == null) "" else when (Types.get(field.type.identifier)) {
            Types.ARRAY -> "Arrays.asList(${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }})"
            Types.VECTOR -> "Arrays.asList(${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }})"
            Types.TUPLE -> {
                val arguments = splitCollection(field.default)
                val firstArgument = handleDefault(Field("", field.type.subtypes[0], arguments[0]))
                val secondArgument = if (arguments.size == 2) {
                    handleDefault(Field("", field.type.subtypes[1], arguments[1]))
                } else {
                    handleDefault(
                        Field(
                            "",
                            Type("tuple", field.type.subtypes.drop(1)),
                            "(${arguments.drop(1).joinToString(",")})"
                        )
                    )
                }

                "new AbstractMap.SimpleEntry<>($firstArgument, $secondArgument)"
            }
            else -> super.handleDefault(field)
        }
}
