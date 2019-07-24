package org.larl.printer

import org.larl.entities.*
import java.io.File

class LuaPrinter(basePath: String) : BasePrinter(basePath) {
    private var currentModule = ""

    override fun print(modules: List<Module>) {
        modules.forEach { module ->
            val moduleFile = File("$basePath/${module.identifier}.lua")
            mkdirp(moduleFile.parentFile)
            moduleFile.writeText(handleModule(module))
        }
    }

    override fun handleModule(module: Module): String {
        currentModule = ""
        val header = module.identifier.removePrefix("/").split("/").dropLast(1).joinToString("\n\n") { mod ->
            currentModule = "$currentModule.$mod".trim('.')

            "if $currentModule == nil then\n  $currentModule = {}\nend"
        }
        val body = module.models.joinToString("\n\n", transform = this::handleModel)

        return "$header\n\n$body\n"
    }

    override fun handleModel(model: Model): String {
        val identifier = "$currentModule.${model.identifier}"
        val fields = model.fields.toList().joinToString(",\n", transform = this::handleField)
        val newHelper =
            "function $identifier:new(o)\n  o = o or {}\n  setmetatable(o, self)\n  self.__index = self\n  return o\nend"

        return "$identifier = {\n$fields\n}\n\n$newHelper"
    }

    override fun handleField(field: Field): String {
        val default = handleDefault(field)

        return "${field.identifier} = $default".prependIndent("  ")
    }

    override fun handleType(type: Type): String = ""

    override fun handleDefault(field: Field): String =
        if (field.default == null) "nil" else when (Types.get(field.type.identifier)) {
            Types.ARRAY -> "{${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[1], def))
            }}}"
            Types.VECTOR -> "{${splitCollection(field.default).joinToString(", ") { def ->
                handleDefault(Field("", field.type.subtypes[0], def))
            }}}"
            Types.TUPLE -> "{${splitCollection(field.default).mapIndexed { index, def ->
                handleDefault(Field("", field.type.subtypes[index], def))
            }.joinToString(", ")}}"
            else -> super.handleDefault(field)
        }
}
