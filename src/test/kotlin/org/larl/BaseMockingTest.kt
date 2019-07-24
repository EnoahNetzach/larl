package org.larl

import org.mockito.Mockito

interface BaseMockingTest {
    fun <T> anyObj(type: Class<T>): T = Mockito.any(type)
}
