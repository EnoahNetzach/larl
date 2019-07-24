package org.larl.printer

import net.pearx.kasechange.toSnakeCase
import org.larl.entities.*
import java.io.File

class RustPrinter(basePath: String) : BasePrinter(basePath) {
    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.rs")
            mkdirp(moduleFile.parentFile)
            moduleFile.writeText("${handleModule(module)}\n")
        }
    }

    override fun handleModule(module: Module): String {
        val nestedModules = module.identifier.removePrefix("/").split("/").dropLast(1)
        val header = "mod ${nestedModules[0]}"

        val body = if (nestedModules.size > 1) {
            handleModule(Module("/${nestedModules.drop(1).joinToString("/")}/ignored", module.models))
        } else {
            module.models.joinToString("\n\n", transform = this::handleModel)
        }

        return trimEmptyLines("$header {\n${body.prependIndent("    ")}\n}")
    }

    override fun handleModel(model: Model): String {
        val fields = model.fields.toList().joinToString("\n", transform = this::handleField)
        val defaults = model.fields.toList().joinToString(
            "\n",
            transform = { field -> "${field.identifier.toSnakeCase()}: ${handleDefault(field)}," })
            .prependIndent("    ")

        val struct = "struct ${model.identifier} {\n$fields\n}"
        val defaultStruct = "Self {\n$defaults\n}".prependIndent("    ")
        val defaultFn = "fn default() -> Self {\n$defaultStruct\n}".prependIndent("    ")
        val defaultTrait = "impl Default for ${model.identifier} {\n$defaultFn\n}"

        return "$struct\n\n$defaultTrait"
    }

    override fun handleField(field: Field): String =
        "${field.identifier.toSnakeCase()}: ${handleType(field.type)},".prependIndent("    ")

    override fun handleType(type: Type): String = when (Types.get(type.identifier)) {
        Types.BOOL -> "bool"
        Types.CHAR -> "char"
        Types.I8 -> "i8"
        Types.I16 -> "i16"
        Types.I32 -> "i32"
        Types.I64 -> "i64"
        Types.ISIZE -> "isize"
        Types.U8 -> "u8"
        Types.U16 -> "u16"
        Types.U32 -> "u32"
        Types.U64 -> "u64"
        Types.USIZE -> "usize"
        Types.F32 -> "f32"
        Types.F64 -> "f64"
        Types.STR -> "String"
        Types.ARRAY -> "[${handleType(type.subtypes[1])}; ${type.subtypes[0]}]"
        Types.VECTOR -> "Vec<${handleType(type.subtypes[0])}>"
        Types.TUPLE -> "(${type.subtypes.joinToString(", ", transform = this::handleType)})"
        else -> super.handleType(type)
    }

    override fun handleDefault(field: Field): String =
        if (field.default == null) "Default::default()" else when (Types.get(field.type.identifier)) {
            Types.STR -> "String::from(${field.default})"
            Types.ARRAY -> "[${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }}]"
            Types.VECTOR -> "vec![${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }}]"
            Types.TUPLE -> "(${field.type.subtypes.zip(splitCollection(field.default)).joinToString(", ") { pair ->
                handleDefault(Field("", pair.first, pair.second))
            }})"
            else -> super.handleDefault(field)
        }
}
