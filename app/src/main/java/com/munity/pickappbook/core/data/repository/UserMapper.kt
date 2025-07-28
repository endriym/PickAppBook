package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints
import com.munity.pickappbook.core.data.remote.model.UserResponse
import com.munity.pickappbook.core.model.User

/* ----------- User to UserResponse ----------- */
fun User.asNetworkModel(): UserResponse =
    UserResponse(
        id = this.id,
        username = this.username,
        displayName = this.displayName
    )

/* ----------- UserResponse to User ----------- */
fun UserResponse.asExternalModel(): User =
    User(
        id = this.id,
        username = this.username,
        displayName = this.displayName,
        profilePictureUrl = ThePlaybookEndpoints.USER_IMAGE_ENDPOINT.format(this.username)
    )
