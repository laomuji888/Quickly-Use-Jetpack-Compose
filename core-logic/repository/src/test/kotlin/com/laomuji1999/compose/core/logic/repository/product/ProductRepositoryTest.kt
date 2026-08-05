package com.laomuji1999.compose.core.logic.repository.product

import com.laomuji1999.compose.core.logic.network.http.HttpService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductRepositoryTest {

    private val httpService: HttpService = mockk()
    private lateinit var repository: ProductRepository

    private fun setupRepository(content: String = "", status: HttpStatusCode = HttpStatusCode.OK) {
        val mockEngine = MockEngine { _ ->
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        every { httpService.client } returns mockClient
        repository = ProductRepository(httpService)
    }

    @Test
    fun `getProducts should return success result when server returns 200`() = runTest {
        setupRepository(
            content = """{"products": [], "total": 0, "skip": 0, "limit": 0}""",
            status = HttpStatusCode.OK
        )

        val result = repository.getProducts(0)

        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertEquals(0, response?.total)
    }

    @Test
    fun `getProducts should use correct skip parameter for pagination`() = runTest {
        var capturedSkip: String? = null
        val mockEngine = MockEngine { request ->
            capturedSkip = request.url.parameters["skip"]
            respond(
                content = """{"products": [], "total": 0, "skip": 10, "limit": 10}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        every { httpService.client } returns mockClient
        repository = ProductRepository(httpService)

        repository.getProducts(1) // Page 1 should be skip 10

        assertEquals("10", capturedSkip)
    }

    @Test
    fun `getProducts should return failure result when server returns 404`() = runTest {
        setupRepository(content = "Not Found", status = HttpStatusCode.NotFound)

        val result = repository.getProducts(0)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getProducts should return failure result when network error occurs`() = runTest {
        val mockEngine = MockEngine { _ ->
            throw Exception("Network Error")
        }
        val mockClient = HttpClient(mockEngine)
        every { httpService.client } returns mockClient
        repository = ProductRepository(httpService)

        val result = repository.getProducts(0)

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getProducts should return failure result when json parsing fails`() = runTest {
        setupRepository(
            content = """{"products": "invalid_type_should_be_array"}""",
            status = HttpStatusCode.OK
        )

        val result = repository.getProducts(0)

        assertTrue(result.isFailure)
    }
}
