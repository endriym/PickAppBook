package com.munity.pickappbook.core.data.remote

import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.Tag
import com.munity.pickappbook.core.data.model.TokenInfo
import com.munity.pickappbook.core.data.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class ThePlaybookDataSource {
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
    }

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    //region User
    suspend fun createUser(user: User): Result<String> {
        val response = httpClient.post(
            urlString = CREATE_USER_ENDPOINT
        ) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(user))
        }

        return checkReturnResult(response) { responseToTransform ->
            TODO()
        }
    }

    suspend fun login(username: String, password: String): Result<TokenInfo> {
        val response = httpClient.post(
            urlString = LOGIN_ENDPOINT
        ) {
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("username", username)
                put("password", password)
            })
        }

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<TokenInfo>()
        }
    }

    suspend fun updateUser(password: String): Result<String> {
        val response = httpClient.put(urlString = UPDATE_USER_ENDPOINT) {
            setBody(buildJsonObject {
                put("password", password)
            })
        }

        return checkReturnResult(response) { responseToTransform ->
            TODO()
        }
    }

    //TODO check how is the result of this request
    suspend fun getUserImage(username: String): Result<ByteArray> {
        val response = httpClient.get(urlString = "$USER_IMAGE_ENDPOINT/$username.jpeg")

        return checkReturnResult(response) { responseToTransform ->
            TODO()
        }
    }
    //endregion

    //region Tag
    suspend fun createTag(name: String, description: String): Result<Tag> {
        val response = httpClient.post(urlString = TAGS_ENDPOINT) {
            setBody(buildJsonObject {
                put("name", name)
                put("description", description)
            })
        }

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<Tag>()
        }
    }

    suspend fun updateTag(tag: Tag): Result<Tag> {
        val response = httpClient.put(urlString = TAGS_ENDPOINT + tag.id) {
            setBody(buildJsonObject {
                put("name", tag.name)
                put("description", tag.description)
            })
        }

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<Tag>()
        }
    }

    suspend fun deleteTag(tag: Tag): Result<String> {
        val response = httpClient.delete(urlString = TAGS_ENDPOINT + tag.id)

        return checkReturnResult(response) { responseToTransform ->
            "Tag successfully deleted"
        }
    }

    suspend fun getTags(): Result<List<Tag>> {
        val response = httpClient.get(urlString = TAGS_ENDPOINT)

        return checkReturnResult(response) { responseToTransform ->
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
        tagIds: List<String>
    ): Result<PickupLine> {
        val response = httpClient.post(urlString = PICKUP_LINES_ENDPOINT) {
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

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun updatePickupLine(
        pickupId: String,
        title: String,
        content: String,
        visible: Boolean,
        starred: Boolean,
        tagIds: List<String>
    ): Result<PickupLine> {
        val response = httpClient.put(urlString = "$PICKUP_LINES_ENDPOINT/$pickupId") {
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

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun deletePickupLine(pickupId: String): Result<String> {
        val response = httpClient.delete(urlString = "$PICKUP_LINES_ENDPOINT/$pickupId")

        return checkReturnResult(response) { responseToTransform ->
            "Pickup line successfully deleted"
        }
    }

    suspend fun getPickupLine(pickupId: String): Result<PickupLine> {
        val response = httpClient.get(urlString = "$PICKUP_LINES_ENDPOINT/$pickupId")

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<PickupLine>()
        }
    }

    suspend fun getPickupLineList(
        page: Int? = null,
        title: String?,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        visibility: PickupLine.Visibility? = null,
        content: String? = null,
    ): Result<List<PickupLine>> {
        val response = httpClient.get(urlString = PICKUP_LINES_ENDPOINT) {
            url {
                parameters.apply {
                    if (page != null)
                        append("page", page.toString())
                    if (title != null)
                        append("title", title)
                    if (starred != null)
                        append("starred", starred.toString())
                    if (visibility != null)
                        append("visibility", visibility.toString())
                    //TODO REVIEEEEWWWWW
                    tagIds?.forEach { tagId ->
                        append("tags[]", tagId)
                    }
                    if (content != null)
                        append("content", content)

                }
            }
        }

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<List<PickupLine>>()
        }
    }

    suspend fun getPickupLineFeed(
        page: Int? = null,
        title: String?,
        tagIds: List<String>? = null,
        visibility: PickupLine.Visibility? = null,
        content: String? = null,
    ): Result<List<PickupLine>> {
        val response = httpClient.get(urlString = PICKUP_LINES_FEED_ENDPOINT) {
            url {
                parameters.apply {
                    if (page != null)
                        append("page", page.toString())
                    if (title != null)
                        append("title", title)
                    if (visibility != null)
                        append("visibility", visibility.toString())
                    //TODO REVIEEEEWWWWW
                    tagIds?.forEach { tagId ->
                        append("tags[]", tagId)
                    }
                    if (content != null)
                        append("content", content)
                }
            }
        }

        return checkReturnResult(response) { responseToTransform ->
            responseToTransform.body<List<PickupLine>>()
        }
    }
    //endregion

    private inline fun <T> checkReturnResult(
        response: HttpResponse,
        onFailureExceptionMessage: String? = null,
        onSuccessTransform: (HttpResponse) -> T,
    ): Result<T> {
        if (response.status.value in 200..299) {
            return Result.success(onSuccessTransform(response))
        }

        if (onFailureExceptionMessage != null)
            return Result.failure(Exception(onFailureExceptionMessage))

        return Result.failure(Exception(response.status.toString()))
    }
}