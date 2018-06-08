package io.ktor.compat


actual fun <T> Set<T>.unmodifiable(): Set<T> = TODO()

actual fun <Value> caseInsensitiveMap(capacity: Int): MutableMap<String, Value> = TODO()
