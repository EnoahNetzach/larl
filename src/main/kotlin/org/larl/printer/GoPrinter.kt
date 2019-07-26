package org.larl.printer

import org.larl.entities.*
import java.io.File

class GoPrinter(basePath: String) : BasePrinter(basePath) {
    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.go")
            mkdirp(moduleFile.parentFile)
            moduleFile.writeText(handleModule(module))
        }
    }

    override fun handleModule(module: Module): String {
        val nestedModules = module.identifier.removePrefix("/").split("/").dropLast(1).takeLast(1)
        val header = "package ${nestedModules[0]}"

        val body = module.models.joinToString("\n\n", transform = this::handleModel)

        return "$header\n\n$body\n"
    }

    override fun handleModel(model: Model): String {
        val fields = model.fields.toList().joinToString("\n", transform = this::handleField)
        val defaults = model.fields.toList().filter { field -> field.default != null }.joinToString(
            "\n",
            transform = { field -> "${field.identifier}: ${handleDefault(field)}," })
            .prependIndent("    ")

        val struct = "type ${model.identifier} struct {\n$fields\n}"
        val defaultStruct = "return ${model.identifier} {\n$defaults\n}".prependIndent("    ")
        val defaultTrait = "func New() ${model.identifier} {\n$defaultStruct\n}"

        return "$struct\n\n$defaultTrait"
    }

    override fun handleField(field: Field): String =
        "${field.identifier} ${handleType(field.type)}".prependIndent("    ")

    override fun handleType(type: Type): String = when (Types.get(type.identifier)) {
        Types.BOOL -> "bool"
        Types.CHAR -> "byte"
        Types.I8 -> "int8"
        Types.I16 -> "int16"
        Types.I32 -> "int32"
        Types.I64 -> "int64"
        Types.ISIZE -> "int"
        Types.U8 -> "uint8"
        Types.U16 -> "uint16"
        Types.U32 -> "uint32"
        Types.U64 -> "uint64"
        Types.USIZE -> "uint"
        Types.F32 -> "float32"
        Types.F64 -> "float64"
        Types.STR -> "string"
        Types.ARRAY -> "[${type.subtypes[0]}]${handleType(type.subtypes[1])}"
        Types.VECTOR -> "[]${handleType(type.subtypes[0])}"
        Types.TUPLE -> "struct {${type.subtypes.mapIndexed { index, t -> "_$index ${handleType(t)}" }.joinToString("; ")}}"
        else -> super.handleType(type)
    }

    override fun handleDefault(field: Field): String =
        if (field.default == null) "" else when (Types.get(field.type.identifier)) {
            Types.ARRAY -> "${handleType(field.type)}{${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }}}"
            Types.VECTOR -> "${handleType(field.type)}{${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }}}"
            Types.TUPLE -> "${handleType(field.type)}{${field.type.subtypes.zip(splitCollection(field.default)).joinToString(
                ", "
            ) { pair ->
                handleDefault(Field("", pair.first, pair.second))
            }}}"
            else -> super.handleDefault(field)
        }
}
