package com.laomuji1999.compose.core.logic.common.cache.impl

import android.content.Context
import android.content.SharedPreferences
import com.laomuji1999.compose.core.logic.common.cache.Cache

internal class CacheAndroid(
    private val context: Context
) : Cache {
    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences("${context.packageName}_cache", Context.MODE_PRIVATE)

    private fun getEditor(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    override fun putInt(key: String, value: Int) {
        val editor = getEditor()
        editor.putInt(key, value)
        editor.commit()
    }

    override fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    override fun putLong(key: String, value: Long) {
        val editor = getEditor()
        editor.putLong(key, value)
        editor.commit()
    }

    override fun getLong(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    override fun putFloat(key: String, value: Float) {
        val editor = getEditor()
        editor.putFloat(key, value)
        editor.commit()
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        val editor = getEditor()
        editor.putBoolean(key, value)
        editor.commit()
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    override fun putDouble(key: String, value: Double) {
        val editor = getEditor()
        editor.putString(key, value.toString())
        editor.commit()
    }

    override fun getDouble(key: String, defValue: Double): Double {
        return sharedPreferences.getString(key, null)?.toDoubleOrNull() ?: defValue
    }

    override fun putString(key: String, value: String?) {
        val editor = getEditor()
        editor.putString(key, value)
        editor.commit()
    }

    override fun getString(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue)!!
    }

}