package com.munity.pickappbook.core.data.remote

import com.munity.pickappbook.core.data.local.datastore.PreferencesStorage
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.CREATE_USER_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.LOGIN_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.PICKUP_LINES_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.REACTION_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.SEARCH_USERS_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.TAGS_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.UPDATE_USER_DISPLAY_NAME_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.USER_IMAGE_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.USER_INFO_BY_ID_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.USER_INFO_ENDPOINT
import com.munity.pickappbook.core.data.remote.model.CreatePickupLineRequest
import com.munity.pickappbook.core.data.remote.model.ErrorResponse
import com.munity.pickappbook.core.data.remote.model.GetPickupLineListRequest
import com.munity.pickappbook.core.data.remote.model.PickupLineListResponse
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagId
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.core.data.remote.model.TokenResponse
import com.munity.pickappbook.core.data.remote.model.UpdatePickupLineRequest
import com.munity.pickappbook.core.data.remote.model.UserListResponse
import com.munity.pickappbook.core.data.remote.model.UserRequest
import com.munity.pickappbook.core.data.remote.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ThePlaybookApi(
    preferencesStorage: PreferencesStorage,
    httpClientProvider: (PreferencesStorage) -> HttpClient = ::thePlayBookHttpClient,
) {
    private val httpClient: HttpClient by lazy { httpClientProvider(preferencesStorage) }

    //region User

    /**
     * Creates a new user.
     *
     * @param username The desired username for the new account
     * @param displayName The desired display name for the new account
     * @param password The desired password for the new account
     * @param image The base 64 encoded string image for the new account
     *
     * @return A [Result.success] containing the [UserInfoResponse] of the
     * new created user if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun createUser(
        username: String,
        displayName: String,
        password: String,
        image: String,
    ): Result<UserResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(CREATE_USER_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(
                UserRequest(
                    username = username,
                    displayName = displayName,
                    password = password,
                    image = image
                )
            )
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<UserResponse>()
        }
    }

    /**
     * Authenticates a user to ThePlaybook with the provided [username] and [password].
     *
     * @return A [Result.success] containing the [TokenResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun login(username: String, password: String): Result<TokenResponse> {
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
            responseToTransform.body<TokenResponse>()
        }
    }

    /**
     * Retrieves the image of the user with the provided [username].
     *
     * @return A [Result.success] containing the image data as a [ByteArray] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getUserImage(username: String): Result<ByteArray> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(USER_IMAGE_ENDPOINT.format(username))
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.bodyAsBytes()
        }
    }

    /**
     * Retrieves (logged in) user info.
     *
     * @return A [Result.success] containing the [UserInfoResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getUserInfo(): Result<UserResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(USER_INFO_ENDPOINT)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<UserResponse>()
        }
    }

    /**
     * Retrieves user info by [userId].
     *
     * @return A [Result.success] containing the [UserInfoResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getUserInfoById(userId: String): Result<UserResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(USER_INFO_BY_ID_ENDPOINT.format(userId))
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<UserResponse>()
        }
    }

    /**
     * Updates the display name of the user.
     *
     * @return A [Result.success] containing the updated [UserInfoResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun updateUserDisplayName(newDisplayName: String): Result<UserResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url(UPDATE_USER_DISPLAY_NAME_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("display_name", newDisplayName)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<UserResponse>()
        }
    }

    suspend fun searchUsers(
        username: String? = null,
        displayName: String? = null,
        page: Int? = null,
    ): Result<UserListResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(SEARCH_USERS_ENDPOINT)
            url {
                with(parameters) {
                    username?.let { append("username", it) }
                    displayName?.let { append("display_name", it) }
                    page?.let { append("page", it.toString()) }
                }
            }
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<UserListResponse>()
        }
    }

    /**
     * Deletes the current user from ThePlaybook.
     *
     * @return A [Result.success] containing `true` if the user was successfully deleted,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun deleteUser(): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Delete
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }
    //endregion

    //region Tag

    /**
     * Creates a new tag with the given [name] and [description].
     *
     * @return A [Result.success] containing the [TagResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun createTag(name: String, description: String): Result<TagResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(TAGS_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("name", name)
                put("description", description)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<TagResponse>()
        }
    }

    /**
     * Updates an existing tag with new tag info.
     *
     * @param tagId The unique identifier of the tag to be updated.
     * @param tagName The new name for the tag. If no change is desired, provide the current name.
     * @param tagDescription The new description for the tag. If no change is desired, provide the current description.
     *
     * @return A [Result.success] containing the updated [TagResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun updateTag(
        tagId: String,
        tagName: String,
        tagDescription: String,
    ): Result<TagResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url(TAGS_ENDPOINT + tagId)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("name", tagName)
                put("description", tagDescription)
            })
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<TagResponse>()
        }
    }

    /**
     * Deletes an existing tag.
     *
     * @param tagId The ID of the tag to delete.
     *
     * @return A [Result.success] containing `true` if the tag was successfully deleted,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
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

    /**
     * Retrieves all tags created by the current user.
     *
     * @return A [Result.success] containing the list of [TagResponse]s if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getTags(): Result<List<TagResponse>> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(TAGS_ENDPOINT)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<List<TagResponse>>()
        }
    }
    //endregion

    //region PickupLines

    /**
     * Creates a new pickup line.
     *
     * @return A [Result.success] containing the new [PickupLineResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun createPickupLine(
        title: String,
        content: String,
        visible: Boolean,
        tagIds: List<String>,
    ): Result<PickupLineResponse> {
        val createPickupLineRequest = CreatePickupLineRequest(
            title = title,
            content = content,
            visible = visible,
            tagIds = tagIds.map { TagId(it) }
        )

        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(PICKUP_LINES_ENDPOINT)
            contentType(ContentType.Application.Json)
            setBody(createPickupLineRequest)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLineResponse>()
        }
    }

    /**
     * Updates an existing pickup line with new tag info.
     *
     * @param pickupLineId The unique identifier of the pickup line to be updated.
     * @param title The new title for the pickup line. If no change is desired, provide the current title.
     * @param content The new content for the pickup line. If no change is desired, provide the current content.
     * @param tagIds The new tags for the pickup line. If no change is desired, provide the current tags.
     * @param isVisible The new visibility for the pickup line. If no change is desired, provide the current visibility.
     *
     * @return A [Result.success] containing the updated [PickupLineResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun updatePickupLine(
        pickupLineId: String,
        title: String,
        content: String,
        tagIds: List<String>?,
        isVisible: Boolean,
    ): Result<PickupLineResponse> {
        val updatedPickupLine = UpdatePickupLineRequest(
            title = title,
            content = content,
            tags = tagIds?.map { TagId(it) },
            isVisible = isVisible
        )

        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url("$PICKUP_LINES_ENDPOINT/${pickupLineId}")
            contentType(ContentType.Application.Json)
            setBody(updatedPickupLine)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLineResponse>()
        }
    }

    /**
     * Deletes an existing pickup line.
     *
     * @param pickupId The ID of the pickup line to delete.
     *
     * @return A [Result.success] containing `true` if the pickup line was successfully deleted,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun deletePickupLine(pickupId: String): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Delete
            url("$PICKUP_LINES_ENDPOINT/$pickupId")
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    /**
     * Retrieves a pickup line with the provided [pickupLineId].
     *
     * @return A [Result.success] containing the [PickupLineResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getPickupLine(pickupLineId: String): Result<PickupLineResponse> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url("$PICKUP_LINES_ENDPOINT/$pickupLineId")
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            responseToTransform.body<PickupLineResponse>()
        }
    }

    /**
     * Updates the current user's reaction for the pickup line
     * identified by [pickupLineId] to [newReaction].
     *
     * @return A [Result.success] containing `true` if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun putReaction(
        pickupLineId: String,
        newReaction: PickupLineResponse.Reaction,
    ): Result<Boolean> {
        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            url(REACTION_ENDPOINT.format(pickupLineId))
            contentType(ContentType.Application.Json)
            setBody(newReaction)
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            true
        }
    }

    /**
     * Retrieves a list of pickup lines based on the provided optional filters.
     *
     * @param title Optional filter to search pickup lines by their title.
     * @param content Optional filter to search pickup lines by their content.
     * @param starred Optional flag to filter pickup lines that are marked as starred.
     * @param tagIds Optional list of tag IDs to filter pickup lines associated with specific tags.
     * @param isVisible Optional visibility status to filter pickup lines by their visibility.
     * The accepted values for this parameter are [GetPickupLineListRequest.Visibility.NOT_VISIBLE],
     * [GetPickupLineListRequest.Visibility.VISIBLE] and [GetPickupLineListRequest.Visibility.ALL]
     * @param successPercentage Optional filter to search for pickup lines with a specific success percentage.
     * @param userId Optional user ID to retrieve pickup lines created by a specific user.
     * @param page Optional pagination parameter to specify which page of results to retrieve.
     *
     * @return A [Result.success] containing the [PickupLineListResponse] if the operation succeeds,
     * or a [Result.failure] containing an [ErrorResponse] if it fails.
     */
    suspend fun getPickupLineList(
        title: String? = null,
        content: String? = null,
        starred: Boolean? = null,
        tagIds: List<String>? = null,
        isVisible: GetPickupLineListRequest.Visibility? = null,
        successPercentage: Double? = null,
        userId: String? = null,
        page: Int? = null,
    ): Result<PickupLineListResponse> {
        val getPickupLineListRequest = GetPickupLineListRequest(
            title = title,
            content = content,
            starred = starred,
            tagIds = tagIds,
            isVisible = isVisible,
            successPercentage = successPercentage,
            userId = userId,
            page = page,
        )

        val httpRequestBuilder = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(PICKUP_LINES_ENDPOINT)
            url {
                getPickupLineListRequest.appendUrlParameters(parameters)
            }
        }

        return checkReturnResult(httpRequestBuilder) { responseToTransform ->
            println(responseToTransform.bodyAsText())
            responseToTransform.body<PickupLineListResponse>()
        }
    }
    //endregion

    /**
     * Executes an HTTP request using the provided [requestBuilder],
     * and returns a [Result] based on the response.
     *
     * @param requestBuilder The HTTP request to perform.
     * @param onSuccessTransform A function applied to the [HttpResponse]
     * if the request is successful (status code 200–299).
     *
     * @return A [Result.success] containing the result of [onSuccessTransform]
     * if the response status code is in the 2xx range.
     * Otherwise, returns a [Result.failure] containing an [ErrorResponse] describing the error.
     *
     * If the request fails due to an exception, that exception is also
     * wrapped in [Result.failure], containing and [ErrorResponse].
     */
    private suspend inline fun <T> checkReturnResult(
        requestBuilder: HttpRequestBuilder,
        onSuccessTransform: (HttpResponse) -> T,
    ): Result<T> {
        val response: HttpResponse = try {
            httpClient.request(requestBuilder)
        } catch (clientEx: ClientRequestException) {
            clientEx.response
        }

        if (response.status.isSuccess())
            return Result.success(value = onSuccessTransform(response))

        val errorResponse = try {
            response.body<ErrorResponse>()
        } catch (jsonEx: JsonConvertException) {
            ErrorResponse(
                code = -1,
                message = jsonEx.message
                    ?: "JsonConvertException: Could not parse something in the response body"
            )
        }

        return Result.failure(exception = errorResponse)
    }
}
