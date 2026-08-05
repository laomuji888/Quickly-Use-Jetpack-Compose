package com.laomuji1999.compose.core.logic.common.cache

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CacheUtilTest {

    private val cache: Cache = mockk()
    private lateinit var cacheUtil: CacheUtil

    @Before
    fun setup() {
        cacheUtil = CacheUtil(cache)
    }

    @Test
    fun `test cacheable property read and write`() {
        // Arrange
        val key = "test_key"
        val defaultValue = "default"
        val newValue = "new_value"
        
        // Mock behavior
        every { cache.getString(key, defaultValue) } returns defaultValue
        every { cache.putString(key, newValue) } just runs
        
        // Use property delegate
        var testProperty by cacheUtil.cacheable(key, defaultValue)

        // Act & Assert: Initial Read
        assertEquals(defaultValue, testProperty)
        
        // Act: Write
        testProperty = newValue
        
        // Assert: Verify cache interaction
        verify { cache.putString(key, newValue) }
        
        // Mock get behavior for updated value
        every { cache.getString(key, defaultValue) } returns newValue
        
        // Assert: Read updated value
        assertEquals(newValue, testProperty)
    }

    @Test
    fun `test boolean cacheable property`() {
        val key = "bool_key"
        every { cache.getBoolean(key, false) } returns false
        every { cache.putBoolean(key, true) } just runs
        
        var boolProperty by cacheUtil.cacheable(key, false)
        
        assertEquals(false, boolProperty)
        
        boolProperty = true
        verify { cache.putBoolean(key, true) }
    }

    @Test
    fun `test numeric cacheable properties`() {
        // Int
        every { cache.getInt("int_key", 0) } returns 0
        every { cache.putInt("int_key", 10) } just runs
        var intProp by cacheUtil.cacheable("int_key", 0)
        assertEquals(0, intProp)
        intProp = 10
        verify { cache.putInt("int_key", 10) }

        // Long
        every { cache.getLong("long_key", 0L) } returns 0L
        every { cache.putLong("long_key", 100L) } just runs
        var longProp by cacheUtil.cacheable("long_key", 0L)
        assertEquals(0L, longProp)
        longProp = 100L
        verify { cache.putLong("long_key", 100L) }

        // Float
        every { cache.getFloat("float_key", 0.0f) } returns 0.0f
        every { cache.putFloat("float_key", 1.5f) } just runs
        var floatProp by cacheUtil.cacheable("float_key", 0.0f)
        assertEquals(0.0f, floatProp)
        floatProp = 1.5f
        verify { cache.putFloat("float_key", 1.5f) }

        // Double
        every { cache.getDouble("double_key", 0.0) } returns 0.0
        every { cache.putDouble("double_key", 2.5) } just runs
        var doubleProp by cacheUtil.cacheable("double_key", 0.0)
        assertEquals(0.0, doubleProp, 1e-10)
        doubleProp = 2.5
        verify { cache.putDouble("double_key", 2.5) }
    }

    @Test(expected = IllegalStateException::class)
    fun `test unsupported type should throw error`() {
        val key = "unsupported_key"
        val defaultValue = listOf("not", "supported")
        
        // This should throw error because List::class is not handled in when(type)
        val unsupportedProp by cacheUtil.cacheable(key, defaultValue)
        
        // Trigger getValue to hit the error
        val value = unsupportedProp
    }

    @Test(expected = IllegalStateException::class)
    fun `test unsupported type should throw error on write`() {
        val key = "unsupported_key"
        val defaultValue = listOf("not", "supported")
        var unsupportedProp by cacheUtil.cacheable(key, defaultValue)
        
        // Trigger setValue to hit the error
        unsupportedProp = listOf("still", "not", "supported")
    }
}
