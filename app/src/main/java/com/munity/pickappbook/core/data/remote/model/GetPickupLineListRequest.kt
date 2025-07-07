package com.munity.pickappbook.core.data.remote.model

import io.ktor.http.ParametersBuilder

data class GetPickupLineListRequest(
    val title: String? = null,
    val content: String? = null,
    val starred: Boolean? = null,
    val tagIds: List<String>? = null,
    val isVisible: Visibility? = null,
    val successPercentage: Double? = null,
    val userId: String? = null,
    val page: Int? = null,
) {
    fun getUrlParameters(): ParametersBuilder = ParametersBuilder().apply {
        title?.let { append("title", title) }
        content?.let { append("content", content) }
        starred?.let { append("starred", starred.toString()) }
        tagIds?.forEach { tagId ->
            append("tags[]", tagId)
        }
        isVisible?.let { append("visibility", isVisible.toString()) }
        successPercentage?.let { append("success_percentage", successPercentage.toString()) }
        userId?.let { append("user_id", userId) }
        page?.let { append("page", page.toString()) }
    }

    enum class Visibility {
        NOT_VISIBLE, VISIBLE, ALL,
    }
}
