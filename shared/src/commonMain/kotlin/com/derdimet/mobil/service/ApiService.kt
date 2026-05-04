package com.derdimet.mobil.service

import com.derdimet.mobil.model.ApiResponse
import com.derdimet.mobil.model.LoginResponse
import com.derdimet.mobil.model.MeResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiService(
    private val baseUrl: String = "https://api.derdimet.com"
) {
    @PublishedApi internal var authToken: String? = null
    @PublishedApi internal val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    fun setAuthToken(token: String?) {
        authToken = token
    }

    @PublishedApi internal fun HttpRequestBuilder.auth() {
        authToken?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        val response: HttpResponse = client.post("/api/auth/login") {
            setBody(mapOf("email" to email, "password" to password))
        }
        
        val result = response.body<LoginResponse>()
        authToken = result.token
        
        return ApiResponse(data = result, success = true)
    }

    suspend fun register(payload: Map<String, Any?>): ApiResponse<Unit> {
        val response: HttpResponse = client.post("/api/register") {
            setBody(payload)
        }
        return if (response.status.isSuccess()) {
            ApiResponse(data = Unit, success = true)
        } else {
            ApiResponse(data = Unit, success = false, message = "API Hatası: ${response.status}")
        }
    }

    suspend fun me(): ApiResponse<MeResponse> {
        val response: HttpResponse = client.get("/api/me") {
            auth()
        }
        return if (response.status.isSuccess()) {
            ApiResponse(data = response.body(), success = true)
        } else {
            ApiResponse(data = response.body(), success = false, message = "API Hatası: ${response.status}")
        }
    }

    suspend inline fun <reified T> get(endpoint: String): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.get(endpoint) {
                auth()
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = response.body(), success = false, message = "API Hatası: ${response.status}")
            }
        } catch (e: Exception) {
            ApiResponse(data = null as T, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> post(endpoint: String, body: Any): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.post(endpoint) {
                auth()
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = response.body(), success = false, message = "API Hatası: ${response.status}")
            }
        } catch (e: Exception) {
            ApiResponse(data = null as T, success = false, message = e.message)
        }
    }
}
