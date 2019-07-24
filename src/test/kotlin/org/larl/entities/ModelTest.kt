package org.larl.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ModelTest {
    @Test
    fun `equals should return true when the models are the same`() {
        val singleModel = Model("single_id")
        assertTrue(singleModel == singleModel)
        assertTrue(Model("id") == Model("id"))
        assertTrue(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ) == Model("id", listOf(Field("name", Type("type"))))
        )
    }

    @Test
    fun `equals should return false when the models are not the same`() {
        assertTrue(Model("id") != Model("other_id"))
        assertTrue(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ) != Model(
                "other_id",
                listOf(Field("name", Type("type")))
            )
        )
        assertTrue(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ) != Model(
                "id",
                listOf(Field("other_name", Type("type")))
            )
        )
        assertTrue(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ) != Model(
                "id",
                listOf(Field("name", Type("other_type")))
            )
        )
    }

    @Test
    fun `models should have the same hashCode when they are the same`() {
        val singleModel = Model("single_id")
        assertEquals(singleModel.hashCode(), singleModel.hashCode())
        assertEquals(Model("id").hashCode(), Model("id").hashCode())
        assertEquals(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ).hashCode(), Model(
                "id",
                listOf(Field("name", Type("type")))
            ).hashCode()
        )
    }

    @Test
    fun `models should not have the same hashCode when they are not the same`() {
        assertNotEquals(Model("id").hashCode(), Model("other_id").hashCode())
        assertNotEquals(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ).hashCode(), Model(
                "other_id",
                listOf(Field("name", Type("type")))
            ).hashCode()
        )
        assertNotEquals(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ).hashCode(), Model(
                "id",
                listOf(Field("other_name", Type("type")))
            ).hashCode()
        )
        assertNotEquals(
            Model(
                "id",
                listOf(Field("name", Type("type")))
            ).hashCode(), Model(
                "id",
                listOf(Field("name", Type("other_type")))
            ).hashCode()
        )
    }
}
