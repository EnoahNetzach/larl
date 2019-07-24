package org.larl.printer

import org.larl.entities.*
import java.io.File

class TypeScriptPrinter(basePath: String) : BasePrinter(basePath) {
    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.ts")
            mkdirp(moduleFile.parentFile)
            moduleFile.writeText(handleModule(module))
        }
    }

    override fun handleModule(module: Module): String {
        val body = module.models.joinToString("\n\n", transform = this::handleModel)

        return "export $body\n"
    }

    override fun handleModel(model: Model): String {
        val fields = model.fields.toList().joinToString("\n\n", transform = this::handleField)

        return "class ${model.identifier} {\n$fields\n}"
    }

    override fun handleField(field: Field): String {
        val optional = if (field.default == null) "?" else ""
        val default = handleDefault(field)

        return "${field.identifier}$optional: ${handleType(field.type)} = $default;".prependIndent("    ")
    }

    override fun handleType(type: Type): String = when (Types.get(type.identifier)) {
        Types.BOOL -> "boolean"
        Types.CHAR, Types.STR -> "string"
        Types.I8, Types.I16, Types.I32, Types.I64, Types.ISIZE,
        Types.U8, Types.U16, Types.U32, Types.U64, Types.USIZE,
        Types.F32, Types.F64 -> "number"
        Types.ARRAY -> "[${List(type.subtypes[0].identifier.toInt()) { handleType(type.subtypes[1]) }.joinToString(", ")}]"
        Types.VECTOR -> "Array<${handleType(type.subtypes[0])}>"
        Types.TUPLE -> "[${type.subtypes.joinToString(", ", transform = this::handleType)}]"
        else -> super.handleType(type)
    }

    override fun handleDefault(field: Field): String =
        if (field.default == null) "undefined" else when (Types.get(field.type.identifier)) {
            Types.ARRAY -> "[${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }}]"
            Types.VECTOR -> "[${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }}]"
            Types.TUPLE -> "[${field.type.subtypes.zip(splitCollection(field.default)).joinToString(", ") { pair ->
                handleDefault(Field("", pair.first, pair.second))
            }}]"
            else -> super.handleDefault(field)
        }
}
