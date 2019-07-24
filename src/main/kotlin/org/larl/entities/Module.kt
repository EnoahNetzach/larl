package org.larl.entities

import java.util.*

class Module(val identifier: String, val models: List<Model> = listOf()) {
    override fun toString(): String {
        return "module $identifier\n\n${models.joinToString("\n")}\n"
    }

    override fun equals(other: Any?): Boolean {
        return other is Module && other.identifier == identifier && other.models == models
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, models)
    }
}
