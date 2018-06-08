package io.ktor.compat

import kotlinx.io.core.*

actual fun Char.isLowerCase(): Boolean {
    fail()
}

actual fun String.toCharArray(): CharArray {
    fail()
}

actual fun encodeBase64(string: String, charset: Charset): String {
    fail()
}

actual fun decodeBase64(string: String, charset: Charset): String {
    fail()
}

internal fun fail(): Nothing = throw Throwable()