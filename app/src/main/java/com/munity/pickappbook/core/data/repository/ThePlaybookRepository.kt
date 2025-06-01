package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.model.User
import com.munity.pickappbook.core.data.remote.ThePlaybookDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ThePlaybookRepository(
    private val pickAppPrefsDS: PickAppPrefsDataSource,
    private val thePlaybookDS: ThePlaybookDataSource,
) {
    val isLoggedIn: Flow<Boolean> = pickAppPrefsDS.storedPreference.map { storedPrefs ->
        !(storedPrefs.user.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
    }

    suspend fun login(username: String, password: String): String {
        val tokenInfo = thePlaybookDS.login(username, password)

        val message: String = when {
            tokenInfo.isSuccess -> {
                val tokenInfoSuccess = tokenInfo.getOrNull()!!
                pickAppPrefsDS.saveNewUser(username = username, password = password)
                pickAppPrefsDS.saveAccessToken(tokenInfoSuccess.token, tokenInfoSuccess.expiration)

                "$username successfully logged in"
            }

            tokenInfo.isFailure -> {
                tokenInfo.exceptionOrNull()!!.message ?: "Something went wrong with the login"
            }

            else -> "Something went wrong with the login"
        }

        return message
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun signup(username: String, password: String, profilePicture: ByteArray): String {
        val profilePictureEncoded = Base64.encode(profilePicture)
        val user = User(username, password, profilePictureEncoded)
        val result = thePlaybookDS.createUser(user)

        val message: String = when {
            result.isSuccess -> {
                "Account successfully created for $username"
            }

            result.isFailure -> {
                result.exceptionOrNull()!!.message
                    ?: "Something went wrong with the signup attempt"
            }

            else -> "Something went wrong with the signup attempt"
        }

        return message
    }

    suspend fun updatePickupLine(pickupLine: PickupLine): Result<PickupLine> {
        return thePlaybookDS.updatePickupLine(pickupLine)
    }

    suspend fun updateReaction(
        pickupLine: PickupLine,
        newReaction: PickupLine.Reaction,
    ): String {
        val result = thePlaybookDS.putReaction(pickupLine.id, newReaction)

        if (result.isSuccess)
            return "Reactions successfully updated"

        return result.exceptionOrNull()!!.message ?: "Reaction not updated"
    }

    suspend fun getPickupLineList(
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): Result<List<PickupLine>> = thePlaybookDS.getPickupLineList(
        page = page,
        title = title,
        tagIds = tagIds,
        isVisible = visibility,
        content = content
    ).map { it.pickupLines }

    suspend fun getPickupLineFeed(
        page: Int? = null,
        title: String? = null,
        tagIds: List<String>? = null,
        visibility: Boolean? = null,
        content: String? = null,
    ): Result<List<PickupLine>> = thePlaybookDS.getPickupLineFeed(
        page = page,
        title = title,
        tagIds = tagIds,
        isVisible = visibility,
        content = content
    ).map { it.pickupLines }
}