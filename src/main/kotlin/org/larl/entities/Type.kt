package org.larl.entities

import java.util.*

class Type(val identifier: String, val subtypes: List<Type> = listOf()) {
    override fun toString(): String {
        val sub = if (subtypes.isEmpty()) "" else "<${subtypes.joinToString(", ")}>"
        return "$identifier$sub"
    }

    override fun equals(other: Any?): Boolean {
        return other is Type && other.identifier == identifier && other.subtypes == subtypes
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, subtypes)
    }
}