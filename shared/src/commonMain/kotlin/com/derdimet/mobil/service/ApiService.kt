package com.derdimet.mobil.service

import com.derdimet.mobil.model.ApiResponse
import com.derdimet.mobil.model.ChangePasswordRequest
import com.derdimet.mobil.model.EmailOnlyRequest
import com.derdimet.mobil.model.LoginResponse
import com.derdimet.mobil.model.MeResponse
import com.derdimet.mobil.model.MessageResponse
import com.derdimet.mobil.model.PasswordResetRequest
import com.derdimet.mobil.model.RefreshTokenRequest
import com.derdimet.mobil.model.UploadedImageResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiService(
    // Android emulator için http://10.0.2.2:8081, iOS/Web için http://localhost:8081 kullanabilirsiniz.
    private val baseUrl: String = "http://10.0.2.2:8081"
) {
    @PublishedApi internal var currentAuthToken: String? = null
    var tokenRefresher: (suspend () -> Boolean)? = null
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
        currentAuthToken = token
    }

    @PublishedApi internal fun HttpRequestBuilder.auth() {
        currentAuthToken?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    @PublishedApi internal fun rethrowIfCancelled(e: Exception): Nothing? {
        if (e is CancellationException) throw e
        return null
    }

    @PublishedApi internal suspend fun errorMessageFrom(response: HttpResponse): String {
        val raw = runCatching { response.bodyAsText() }.getOrNull().orEmpty()
        if (raw.isBlank()) return "API Hatası: ${response.status}"
        return runCatching {
            val json = Json.parseToJsonElement(raw).jsonObject
            json["message"]?.jsonPrimitive?.content
                ?: when {
                    response.status.value == 404 && json["error"]?.jsonPrimitive?.content == "Not Found" ->
                        "İşlem bulunamadı. Backend yeniden başlatıldı mı?"
                    else -> json["error"]?.jsonPrimitive?.content
                }
        }.getOrNull()?.takeIf { it.isNotBlank() }
            ?: "API Hatası: ${response.status}"
    }

    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        val response: HttpResponse = client.post("/api/auth/login") {
            setBody(mapOf("email" to email, "password" to password))
        }

        if (response.status.isSuccess()) {
            val result = response.body<LoginResponse>()
            currentAuthToken = result.token
            return ApiResponse(data = result, success = true)
        }

        val raw = response.bodyAsText()
        val message = try {
            Json.parseToJsonElement(raw).jsonObject["message"]?.jsonPrimitive?.content
                ?: "API Hatası: ${response.status}"
        } catch (_: Exception) {
            "API Hatası: ${response.status}"
        }
        return ApiResponse(
            data = LoginResponse(token = "", tokenType = "Bearer"),
            success = false,
            message = message
        )
    }

    suspend fun refresh(refreshToken: String): ApiResponse<LoginResponse> {
        val response: HttpResponse = client.post("/api/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken))
        }
        return if (response.status.isSuccess()) {
            val result = response.body<LoginResponse>()
            currentAuthToken = result.token
            ApiResponse(data = result, success = true)
        } else {
            ApiResponse(data = null, success = false, message = errorMessageFrom(response))
        }
    }

    suspend fun logout(refreshToken: String): ApiResponse<MessageResponse> {
        val response: HttpResponse = client.post("/api/auth/logout") {
            setBody(RefreshTokenRequest(refreshToken))
        }
        return if (response.status.isSuccess()) {
            ApiResponse(data = response.body(), success = true)
        } else {
            ApiResponse(data = null, success = false, message = errorMessageFrom(response))
        }
    }

    @PublishedApi internal suspend fun refreshAuthIfNeeded(response: HttpResponse, allowRetry: Boolean): Boolean {
        if (response.status.value == 401 && allowRetry) {
            return tokenRefresher?.invoke() == true
        }
        return false
    }

    suspend fun forgotPassword(email: String): ApiResponse<MessageResponse> {
        return post("/api/auth/password/forgot", EmailOnlyRequest(email.trim()))
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): ApiResponse<MessageResponse> {
        return post(
            "/api/auth/password/reset",
            PasswordResetRequest(email.trim(), code.trim(), newPassword),
        )
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): ApiResponse<MessageResponse> {
        return post<MessageResponse>(
            "/api/auth/password/change",
            ChangePasswordRequest(currentPassword, newPassword),
        )
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
            ApiResponse(data = null, success = false, message = errorMessageFrom(response))
        }
    }

    suspend inline fun <reified T> delete(endpoint: String): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.delete(endpoint) {
                auth()
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> get(endpoint: String, allowRetry: Boolean = true): ApiResponse<T> {
        return try {
            var response: HttpResponse = client.get(endpoint) { auth() }
            if (refreshAuthIfNeeded(response, allowRetry)) {
                response = client.get(endpoint) { auth() }
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> put(endpoint: String, body: Any): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.put(endpoint) {
                auth()
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> patch(endpoint: String, body: Any): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.patch(endpoint) {
                auth()
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> post(endpoint: String, body: Any, allowRetry: Boolean = true): ApiResponse<T> {
        return try {
            var response: HttpResponse = client.post(endpoint) {
                auth()
                setBody(body)
            }
            if (refreshAuthIfNeeded(response, allowRetry)) {
                response = client.post(endpoint) {
                    auth()
                    setBody(body)
                }
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    suspend inline fun <reified T> postEmpty(endpoint: String): ApiResponse<T> {
        return try {
            val response: HttpResponse = client.post(endpoint) {
                auth()
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(data = null, success = false, message = errorMessageFrom(response))
            }
        } catch (e: Exception) {
            rethrowIfCancelled(e)
            ApiResponse(data = null, success = false, message = e.message)
        }
    }

    /** Tek bir görseli `/api/media/images` endpoint'ine multipart olarak yükler. */
    suspend fun uploadImage(
        bytes: ByteArray,
        filename: String,
        contentType: String,
    ): ApiResponse<UploadedImageResponse> {
        return try {
            val response: HttpResponse = client.post("/api/media/images") {
                auth()
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "file",
                                value = bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, contentType)
                                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                                },
                            )
                        }
                    )
                )
            }
            if (response.status.isSuccess()) {
                ApiResponse(data = response.body(), success = true)
            } else {
                ApiResponse(
                    data = UploadedImageResponse(url = ""),
                    success = false,
                    message = "API Hatası: ${response.status}",
                )
            }
        } catch (e: Exception) {
            ApiResponse(data = UploadedImageResponse(url = ""), success = false, message = e.message)
        }
    }
}
