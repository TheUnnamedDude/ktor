package io.ktor.client.tests.features.cookies

import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.client.tests.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.experimental.*
import org.junit.*
import org.junit.Assert.*

class CookiesTest {

    @Test
    fun compatibilityTest() = runBlocking<Unit> {
        val client = HttpClient(MockEngine.check {
            assertEquals("*/*", headers[HttpHeaders.Accept])
            val rawCookies = headers[HttpHeaders.Cookie]!!
            assertFalse(rawCookies.contains("x-enc"))

            assertEquals(1, headers.getAll(HttpHeaders.Cookie)?.size!!)
            val cookies = parseClientCookiesHeader(rawCookies)

            assertEquals(2, cookies.size)
            assertEquals(encodeURLPart("1,2,3,4"), cookies["first"])
            assertEquals("abc", cookies["second"])
        }).config {
            install(HttpCookies) {
                default {
                    addCookie("localhost", Cookie("first", "1,2,3,4"))
                    addCookie("localhost", Cookie("second", "abc"))
                }
            }
        }

        client.get<HttpResponse>()
    }
}
