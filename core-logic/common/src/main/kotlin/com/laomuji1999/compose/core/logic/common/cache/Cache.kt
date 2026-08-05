package com.laomuji1999.compose.core.logic.common.cache

interface Cache {
    fun putInt(key: String, value: Int)

    fun getInt(key: String, defValue: Int):Int

    fun putLong(key: String, value: Long)

    fun getLong(key: String, defValue: Long):Long

    fun putFloat(key: String, value: Float)

    fun getFloat(key: String, defValue: Float):Float

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defValue: Boolean):Boolean

    fun putDouble(key: String, value: Double)

    fun getDouble(key: String, defValue: Double):Double

    fun putString(key: String, value: String?)

    fun getString(key: String, defValue: String):String
}