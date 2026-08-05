package com.laomuji1999.compose.core.logic.common.cache

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class CacheUtil(
    val cache: Cache
) {

    inline fun <reified T:Any> cacheable(key:String,dftValue:T) = Cacheable(key,dftValue,T::class, cache)

    @Suppress("UNCHECKED_CAST")
    class Cacheable<T:Any> (
        private val key:String,
        private val dftValue:T,
        private val type:KClass<T>,
        private val cache: Cache
    ): ReadWriteProperty<Any?,T> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            when(type){
                Int::class -> cache.putInt(key, value as Int)
                Long::class -> cache.putLong(key, value as Long)
                Float::class -> cache.putFloat(key, value as Float)
                Boolean::class -> cache.putBoolean(key, value as Boolean)
                Double::class -> cache.putDouble(key, value as Double)
                String::class -> cache.putString(key, value as String)
                else -> error("type is not support")
            }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return when(type){
                Int::class -> cache.getInt(key, dftValue as Int)
                Long::class -> cache.getLong(key, dftValue as Long)
                Float::class -> cache.getFloat(key, dftValue as Float)
                Boolean::class -> cache.getBoolean(key, dftValue as Boolean)
                Double::class -> cache.getDouble(key, dftValue as Double)
                String::class -> cache.getString(key, dftValue as String)
                else -> error("type is not support")
            } as T
        }
    }
}