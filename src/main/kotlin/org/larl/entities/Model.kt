package org.larl.entities

import java.util.*

class Model(val identifier: String, val fields: List<Field> = listOf()) {
    override fun toString(): String {
        return "$identifier {\n${fields.joinToString("\n")}\n}"
    }

    override fun equals(other: Any?): Boolean {
        return other is Model && other.identifier == identifier && other.fields == fields
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, fields)
    }
}
