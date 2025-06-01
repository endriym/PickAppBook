package com.munity.pickappbook.core.data.remote

import android.util.Log
import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import com.munity.pickappbook.core.data.model.ErrorInfo
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.PickupLineResponse
import com.munity.pickappbook.core.data.model.Tag
import com.munity.pickappbook.core.data.model.TokenInfo
import com.munity.pickappbook.core.data.model.User
import com.munity.pickappbook.util.DateUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.takeFrom
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class ThePlaybookDataSource(
    val pickAppPrefsDataSource: PickAppPrefsDataSource,
) {
    companion object {
        private const val BASE_URL = ""
        private const val CREATE_USER_ENDPOINT = "$BASE_URL/user"
        private const val UPDATE_USER_ENDPOINT = "$BASE_URL/api/user"
        private const val USER_IMAGE_ENDPOINT = "$BASE_URL/images/%s.jpeg"
        private const val LOGIN_ENDPOINT = "$BASE_URL/login"
        private const val REFRESH_ENDPOINT = "$BASE_URL/auth/refresh_token"
        private const val TAGS_ENDPOINT = "$BASE_URL/api/tags"
        private const val PICKUP_LINES_ENDPOINT = "$BASE_URL/api/pickup-lines"
        private const val PICKUP_LINES_FEED_ENDPOINT = "$PICKUP_LINES_ENDPOINT/feed"
        private const val REACTION_ENDPOINT = "$BASE_URL/api/pickup-lines/%s/reaction"
        private const val TAG = "ThePlaybookDataSource"
    }

    private val refreshMutex = Mutex()

    private val httpClient: HttpClient = HttpClient(CIO) {
        expectSuccess = true

        install(ContentNegotiation) {
            json()
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val storedPrefs = pickAppPrefsDataSource.storedPreference.first()
                    val accessToken = storedPrefs.accessToken
                    val expiration = storedPrefs.expiration

                    if (expiration != null && DateUtil.isIso8601Expired(expiration)) {
                        Log.d(TAG, "loadTokens: accessToken is expired")
                        return@loadTokens null
                    }

                    if (accessToken != null)
                        return@loadTokens BearerTokens(accessToken, null)

                    Log.d(TAG, "loadTokens: accessToken is null")
                    return@loadTokens null
                }

                // return false for urls which don't need an Authorization header
                // (ex: token or refresh token urls),
                // otherwise return true to include the Authorization header
                sendWithoutRequest { request ->
                    request.url.buildString() != CREATE_USER_ENDPOINT &&
                            request.url.buildString() != LOGIN_ENDPOINT &&
                            request.url.buildString().contains("$BASE_URL/images")
                }
            }
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                val clientException = exception as? ClientRequestException
                    ?: return@handleResponseExceptionWithRequest
                val response = clientException.response

                if (response.status == HttpStatusCode.Unauthorized) {
                    val wwwAuthenticateHeader = response.headers[HttpHeaders.WWWAuthenticate]
                    if (wwwAuthenticateHeader?.startsWith(
                            "JWT realm=", ignoreCase = true
                        ) == true
                    ) {
                        println("Received 401 with WWW-Authenticate: JWT realm=. Attempting token refresh.")

                        // Prevent multiple concurrent refresh attempts
                        refreshMutex.withLock {
                            val storedPreferences = pickAppPrefsDataSource.storedPreference.first()

                            val newToken = try {
                                val refreshResponse = refreshTokenRequest(
                                    storedPreferences.user!!, storedPreferences.password!!
                                )
                                if (refreshResponse.status.isSuccess())
                                    refreshResponse.body<TokenInfo>()
                                else {
                                    println("Refresh token request failed with status: ${refreshResponse.status}")
                                    // TODO: Trigger user logout here if refresh failed (refresh token invalid)
                                    throw ClientRequestException(
                                        refreshResponse,
                                        refreshResponse.status.description
                                    )
                                }
                            } catch (e: Exception) {
                                println("Error during token refresh: $e")
                                // TODO: Trigger user logout here if refresh failed (e.g., network error)
                                throw e
                            }

                            // Save new token
                            pickAppPrefsDataSource.saveAccessToken(
                                newToken.token, newToken.expiration
                            )
                            println("Token refreshed successfully. Retrying original request.")

                            // Retry the original request with the new access token
                            val newRequest = HttpRequestBuilder().takeFrom(request)
                            newRequest.headers.remove(HttpHeaders.Authorization)
                            newRequest.headers.append(
                                HttpHeaders.Authorization, "Bearer ${newToken.token}"
                            )
                            val newResponse = httpClient.request(newRequest)

                            // Throw the new response to indicate that the request was successfully retried
                            throw HandledResponseException(
                                newResponse, newResponse.status.description
                            )
                        }
                    } else {
                        // It's a 401, but not a JWT realm. Let Ktor's default error handling or
                        // other interceptors take over
                        println("Received 401, but not a JWT realm. Propagating exception.")
                        throw clientException
                    }
                }
            }
        }
    }

    //region User
    suspend fun createUser(user: User): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(CREATE_USER_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(user))
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    /**
     * Login to ThePlaybook via a [username] and a [password].
     *
     * @return a [TokenInfo] wrapped in a [Result.success] if the response is ok (200..299).
     * Otherwise a [Result.failure], encapsulating an Exception, is returned.
     * The Exception's message will be the response status.
     */
    suspend fun login(username: String, password: String): Result<TokenInfo> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(LOGIN_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("username", username)
                put("password", password)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<TokenInfo>()
        }
    }

    /**
     * Refresh the token via a [username] and a [password].
     *
     * @return a [TokenInfo] wrapped in a [Result.success] if the response is ok (200..299).
     * Otherwise a [Result.failure], encapsulating an Exception, is returned.
     * The Exception's message will be the response status.
     */
    suspend fun refreshTokenRequest(username: String, password: String) = httpClient.post(
        urlString = LOGIN_ENDPOINT
    ) {
        contentType(ContentType.Application.Json)
        setBody(buildJsonObject {
            put("username", username)
            put("password", password)
        })
    }

    /**
     * Updates user's password.
     * The user, whose password is being changed, is identified by the token.
     *
     * @param newPassword the new password to be set
     */
    suspend fun updateUser(newPassword: String): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(UPDATE_USER_ENDPOINT)
            setBody(buildJsonObject {
                put("password", newPassword)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    suspend fun getUserImage(username: String): Result<ByteArray> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(USER_IMAGE_ENDPOINT.format(username))
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.bodyAsBytes()
        }
    }
    //endregion

    //region Tag

    /**
     * Creates a new tag with the given [name] and [description].
     *
     * @return a [Tag] wrapped in a [Result.success] , if response is ok (200..299).
     * Otherwise a [Result.failure], encapsulating an Exception, is returned.
     * The Exception's message will be the response status.
     */
    suspend fun createTag(name: String, description: String): Result<Tag> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(TAGS_ENDPOINT)
            setBody(buildJsonObject {
                put("name", name)
                put("description", description)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<Tag>()
        }
    }

    /**
     * Updates an existing tag with new tag info.
     *
     * @param newTag [Tag] object with a possible new [Tag.name] and/or [Tag.description].
     * `newTag` contains the [Tag.id] to identify the correct tag.
     *
     * @return the updated [Tag] wrapped in a [Result.success] instance if response is ok (200..299).
     * Otherwise a [Result.failure], encapsulating an Exception, is returned.
     * The Exception's message will be the response status.
     */
    suspend fun updateTag(newTag: Tag): Result<Tag> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url(TAGS_ENDPOINT + newTag.id)
            setBody(buildJsonObject {
                put("name", newTag.name)
                put("description", newTag.description)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<Tag>()
        }
    }

    /**
     * Deletes an existing tag.
     *
     * @param tagId the ID of the tag to delete.
     *
     * @return a confirmation message if tag was successfully deleted.
     * Otherwise a [Result.failure], encapsulating an Exception, is returned.
     * The Exception's message will be the response status.
     */
    suspend fun deleteTag(tagId: String): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Delete
            url(TAGS_ENDPOINT + tagId)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    suspend fun getTags(): Result<List<Tag>> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(TAGS_ENDPOINT)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<List<Tag>>()
        }
    }
    //endregion

    //region PickupLines
    suspend fun createPickupLine(
        title: String,
        content: String,
        visible: Boolean,
        starred: Boolean,
        tagIds: List<String>,
    ): Result<PickupLine> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(PICKUP_LINES_ENDPOINT)
            setBody(buildJsonObject {
                put("title", title)
                put("content", content)
                put("visible", visible)
                put("starred", starred)
                putJsonArray("tags") {
                    tagIds.forEach { tagId ->
                        add(buildJsonObject {
                            put("id", tagId)
                        })
                    }
                }
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun updatePickupLine(pickupLine: PickupLine): Result<PickupLine> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url("$PICKUP_LINES_ENDPOINT/${pickupLine.id}")
            setBody(Json.encodeToString(pickupLine))
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun deletePickupLine(pickupId: String): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Delete
            url("$PICKUP_LINES_ENDPOINT/$pickupId")
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    suspend fun getPickupLine(pickupId: String): Result<PickupLine> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url("$PICKUP_LINES_ENDPOINT/$pickupId")
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun putReaction(
        pickupLineId: String,
        newReaction: PickupLine.Reaction,
    ): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url(REACTION_ENDPOINT.format(pickupLineId))
            setBody(newReaction)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    suspend fun getPickupLineList(
        page: Int? = null,
        title: String? = null,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        isVisible: Boolean? = null,
        content: String? = null,
    ): Result<PickupLineResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(PICKUP_LINES_ENDPOINT)
            url {
                parameters.apply {
                    if (page != null)
                        append("page", page.toString())
                    if (title != null)
                        append("title", title)
                    if (starred != null)
                        append("starred", starred.toString())
                    if (isVisible != null)
                        append("visibility", isVisible.toString())
                    tagIds?.forEach { tagId ->
                        append("tags[]", tagId)
                    }
                    if (content != null)
                        append("content", content)
                }
            }
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLineResponse>()
        }
    }

    suspend fun getPickupLineFeed(
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        isVisible: Boolean? = null,
        content: String? = null,
    ): Result<PickupLineResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(PICKUP_LINES_FEED_ENDPOINT)
            url {
                parameters.apply {
                    if (page != null)
                        append("page", page.toString())
                    if (title != null)
                        append("title", title)
                    if (isVisible != null)
                        append("visibility", isVisible.toString())
                    tagIds?.forEach { tagId ->
                        append("tags[]", tagId)
                    }
                    if (content != null)
                        append("content", content)
                }
            }
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLineResponse>()
        }
    }
    //endregion

    /**
     * @return a [Result.success] containing the output of
     * [onSuccessTransform] applied to the `response` of the request
     * done with [requestBuilder].
     * if `response`'s status code is within the 200..299 range.
     *
     * If the status code falls outside this range,
     * a [Result.failure] is returned with a [java.lang.Exception].
     * The exception message will be [onFailureExceptionMessage]
     * if provided; otherwise, it defaults to `response.status`.
     */
    private suspend inline fun <T> checkReturnResult(
        requestBuilder: HttpRequestBuilder,
        onSuccessTransform: (HttpResponse) -> T,
    ): Result<T> {
        val response: HttpResponse = try {
            httpClient.request(requestBuilder)
        } catch (handledEx: HandledResponseException) {
            handledEx.response
        } catch (clientEx: ClientRequestException) {
            clientEx.response
        }

        Log.d(TAG, "checkReturnResult: ${response.bodyAsText()}")

        if (response.status.value in 200..299)
            return Result.success(onSuccessTransform(response))

        val errorInfo = try {
            response.body<ErrorInfo>()
        } catch (jsonEx: JsonConvertException) {
            ErrorInfo(response.status.value, response.status.description)
        }
        return Result.failure(Exception(errorInfo.toString()))
    }
}