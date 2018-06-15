package io.ktor.compat

import kotlinx.io.charsets.*

actual fun Char.isLowerCase(): Boolean = true

actual fun String.toCharArray(): CharArray {
    TODO()
}

actual fun encodeBase64(string: String, charset: Charset): String {
    TODO()
}

actual fun decodeBase64(string: String, charset: Charset): String {
    TODO()
}
