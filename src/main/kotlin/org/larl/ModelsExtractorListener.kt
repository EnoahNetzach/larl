package org.larl

import larl.grammar.LarlBaseListener
import larl.grammar.LarlParser
import org.antlr.v4.runtime.ParserRuleContext
import org.larl.entities.Field
import org.larl.entities.Model
import org.larl.entities.Module
import org.larl.entities.Type

class ModelsExtractorListener : LarlBaseListener() {
    private open class MutableType(val subtypes: MutableList<ChildType> = mutableListOf())
    private class RootType : MutableType()
    private class ChildType(val identifier: String, val parent: MutableType) : MutableType()

    private val activeModules = mutableMapOf<String, Module>()
    private var activeModuleIdentifier: String? = null
    private val activeModels = mutableMapOf<String, Model>()
    private var activeModelIdentifier: String? = null
    private var activeFields = mutableMapOf<String, Field>()
    private var activeFieldIdentifier: String? = null
    private var activeFieldType: MutableType = RootType()
    private var activeFieldDefault: String? = null

    val modules: List<Module> get() = activeModules.values.toList()

    override fun enterModule(ctx: LarlParser.ModuleContext?) {
        assert(activeModuleIdentifier == null) { "While entering a module, the active module identifier should be null." }
        assert(activeModels.isEmpty()) { "While entering a module, there should be no active models." }
        assert(ctx != null)
        assertChildCount(ctx, 5, "module")

        activeModuleIdentifier = ctx!!.getChild(2).text
    }

    override fun enterModel(ctx: LarlParser.ModelContext?) {
        assert(activeModelIdentifier == null) { "While entering a model, the active model identifier should be null." }
        assert(activeFields.isEmpty()) { "While entering a model, there should be no active fields." }
        assert(ctx != null)
        assertChildCount(ctx, 3, "model")

        activeModelIdentifier = ctx!!.getChild(0).text
    }

    override fun enterFieldWithoutDefault(ctx: LarlParser.FieldWithoutDefaultContext?) {
        assert(activeFieldIdentifier == null) { "While entering a field, the active field identifier should be null." }
        assert(ctx != null)
        assertChildCount(ctx, 3, "field")

        activeFieldIdentifier = ctx!!.getChild(2).text
    }

    override fun enterTypeDefinition(ctx: LarlParser.TypeDefinitionContext?) {
        assert(activeFieldType is RootType) { "While entering a type definition, the active field type should be null." }
        assert(ctx != null)
        assertChildCount(ctx, 1, "type definition")
    }

    override fun enterArrayType(ctx: LarlParser.ArrayTypeContext?) {
        assert(ctx != null)
        assertChildCount(ctx, 7, "array type")

        val arrayType = ChildType(ctx!!.getChild(0).text, activeFieldType)
        arrayType.subtypes += ChildType(ctx.getChild(5).text, arrayType)
        activeFieldType.subtypes += arrayType
        activeFieldType = arrayType
    }

    override fun exitArrayType(ctx: LarlParser.ArrayTypeContext?) {
        assert(activeFieldType is ChildType) { "While exiting an array type, the active field type should be a child." }
        assert(activeFieldType.subtypes.size == 2) { "While exiting an array type, the active field type should have exactly 1 children." }

        activeFieldType = (activeFieldType as ChildType).parent
    }

    override fun enterVectorType(ctx: LarlParser.VectorTypeContext?) {
        assert(ctx != null)
        assertChildCount(ctx, 4, "vector type")

        val arrayType = ChildType(ctx!!.getChild(0).text, activeFieldType)
        activeFieldType.subtypes += arrayType
        activeFieldType = arrayType
    }

    override fun exitVectorType(ctx: LarlParser.VectorTypeContext?) {
        assert(activeFieldType is ChildType) { "While exiting a vector type, the active field type should be a child." }
        assert(activeFieldType.subtypes.size == 1) { "While exiting a vector type, the active field type should have exactly 1 child." }

        activeFieldType = (activeFieldType as ChildType).parent
    }

    override fun enterTupleType(ctx: LarlParser.TupleTypeContext?) {
        assert(ctx != null)
        assertChildCount(ctx, { childCount -> childCount >= 7 && (childCount - 7) % 3 == 0 }, "tuple type")

        val arrayType = ChildType(ctx!!.getChild(0).text, activeFieldType)
        activeFieldType.subtypes += arrayType
        activeFieldType = arrayType
    }

    override fun exitTupleType(ctx: LarlParser.TupleTypeContext?) {
        assert(activeFieldType is ChildType) { "While exiting a tuple type, the active field type should be a child." }
        assert(activeFieldType.subtypes.isNotEmpty()) { "While exiting a tuple type, the active field type should not be empty." }

        activeFieldType = (activeFieldType as ChildType).parent
    }

    override fun enterBuiltinType(ctx: LarlParser.BuiltinTypeContext?) {
        assert(ctx != null)
        assertChildCount(ctx, 1, "builtin type")

        val builtinType = ChildType(ctx!!.getChild(0).text, activeFieldType)
        activeFieldType.subtypes += builtinType
    }

    override fun enterCustomType(ctx: LarlParser.CustomTypeContext?) {
        assert(ctx != null)
        assertChildCount(ctx, 1, "custom type")

        val builtinType = ChildType(ctx!!.getChild(0).text, activeFieldType)
        activeFieldType.subtypes += builtinType
    }

    override fun enterDefaultArgument(ctx: LarlParser.DefaultArgumentContext?) {
        assert(activeFieldDefault == null) { "While entering a default argument, the active field default should be null." }
        assert(ctx != null)
        assertChildCount(ctx, 1, "default argument")

        activeFieldDefault = ctx!!.getChild(0).text.trim('{', '}')
    }

    override fun exitField(ctx: LarlParser.FieldContext?) {
        assert(activeFieldIdentifier != null) { "While exiting a field, the active field identifier should not be null." }
        assert(activeFieldType is RootType) { "While exiting a field, the active field type should be the root." }
        assert(activeFieldType.subtypes.isNotEmpty()) { "While exiting a field, the active field type should not be empty." }

        activeFields[activeFieldIdentifier!!] =
            Field(
                activeFieldIdentifier!!,
                extractType(activeFieldType.subtypes[0]),
                activeFieldDefault
            )

        activeFieldIdentifier = null
        activeFieldType = RootType()
        activeFieldDefault = null
    }

    override fun exitModel(ignored: LarlParser.ModelContext?) {
        assert(activeModelIdentifier != null) { "While exiting a model, the active model identifier should not be null." }

        activeModels[activeModelIdentifier!!] =
            Model(activeModelIdentifier!!, activeFields.values.toList())

        activeFields.clear()
        activeModelIdentifier = null
    }

    override fun exitModule(ignored: LarlParser.ModuleContext?) {
        assert(activeModuleIdentifier != null) { "While exiting a module, the active module identifier should not be null." }
        assert(activeModels.isNotEmpty()) { "While exiting a module, there should be some active models." }

        activeModules[activeModuleIdentifier!!] =
            Module(activeModuleIdentifier!!, activeModels.values.toList())

        activeModels.clear()
        activeModuleIdentifier = null
    }

    private fun assertChildCount(ctx: ParserRuleContext?, expected: Int, name: String) {
        val childCount = ctx!!.childCount
        assert(childCount == expected) { "The $name context should have exactly $expected children, $childCount found." }
    }

    private fun assertChildCount(ctx: ParserRuleContext?, expect: (Int) -> Boolean, name: String) {
        val childCount = ctx!!.childCount
        assert(expect(childCount)) { "The $name context should have the expected child count, $childCount found." }
    }

    private fun extractType(tempType: ChildType): Type {
        return Type(tempType.identifier, tempType.subtypes.map(this::extractType))
    }
}
