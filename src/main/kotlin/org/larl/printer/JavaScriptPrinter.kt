package org.larl.printer

import org.larl.entities.*
import java.io.File

class JavaScriptPrinter(basePath: String) : BasePrinter(basePath) {
    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.js")
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
        val default = handleDefault(field)

        return "${field.identifier} = $default;".prependIndent("    ")
    }

    override fun handleType(type: Type): String = ""

    override fun handleDefault(field: Field): String =
        if (field.default == null) "undefined" else when (Types.get(field.type.identifier)) {
            Types.ARRAY -> "[${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }}]"
            Types.VECTOR -> "[${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }}]"
            Types.TUPLE -> "[${splitCollection(field.default).mapIndexed { index, def ->
                handleDefault(Field("", field.type.subtypes[index], def))
            }.joinToString(", ")}]"
            else -> super.handleDefault(field)
        }
}
