package com.munity.pickappbook.core.data.remote

public object ThePlaybookEndpoints {
    const val BASE_URL = "/api"
    const val CREATE_USER_ENDPOINT = "$BASE_URL/user"
    const val SEARCH_USERS_ENDPOINT = "$BASE_URL/user"
    const val UPDATE_USER_DISPLAY_NAME_ENDPOINT = "$BASE_URL/user/name"
    const val USER_INFO_ENDPOINT = "$BASE_URL/user/info"
    const val USER_IMAGE_ENDPOINT = "$BASE_URL/images/%s.jpeg"
    const val LOGIN_ENDPOINT = "$BASE_URL/login"
    const val REFRESH_ENDPOINT = LOGIN_ENDPOINT
    const val TAGS_ENDPOINT = "$BASE_URL/tags"
    const val PICKUP_LINES_ENDPOINT = "$BASE_URL/pickup-lines"
    const val PICKUP_LINES_FEED_ENDPOINT = "$PICKUP_LINES_ENDPOINT/feed"
    const val REACTION_ENDPOINT = "$BASE_URL/pickup-lines/%s/reaction"

    fun userImageUrl(username: String) = USER_IMAGE_ENDPOINT.format(username)
}
