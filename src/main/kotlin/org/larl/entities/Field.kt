package org.larl.entities

import java.util.*

class Field(val identifier: String, val type: Type, val default: String? = null) {
    override fun toString(): String {
        val def = if (default == null) "" else " = $default"
        return "  $type: $identifier$def"
    }

    override fun equals(other: Any?): Boolean {
        return other is Field && other.type == type && other.identifier == identifier && other.default == default
    }

    override fun hashCode(): Int {
        return Objects.hash(type, identifier, default)
    }
}
