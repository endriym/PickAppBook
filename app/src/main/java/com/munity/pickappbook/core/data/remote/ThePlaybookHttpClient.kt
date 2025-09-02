package com.munity.pickappbook.core.data.remote

import com.munity.pickappbook.core.data.local.datastore.PreferencesStorage
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.BASE_URL
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.CREATE_USER_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.LOGIN_ENDPOINT
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints.REFRESH_ENDPOINT
import com.munity.pickappbook.core.data.remote.model.TokenResponse
import com.munity.pickappbook.util.DateUtil
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun thePlayBookHttpClient(preferencesStorage: PreferencesStorage): HttpClient {
    val refreshTokenClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

        install(Auth) {
            jwt {
                realm = "Playbook"

                loadTokens {
                    val storedPrefs = preferencesStorage.storedPreferences.first()
                    val accessToken = storedPrefs.accessToken
                    val expiration = storedPrefs.expiration

                    if (expiration != null && DateUtil.isIso8601Expired(expiration))
                        return@loadTokens null

                    if (accessToken != null)
                        return@loadTokens BearerTokens(accessToken, null)

                    return@loadTokens null
                }

                refreshTokens {
                    val storedPreferences = preferencesStorage.storedPreferences.first()

                    if (storedPreferences.username == null || storedPreferences.password == null)
                        throw ClientRequestException(response, response.status.description)

                    val refreshedTokenResponse = refreshTokenClient.post(urlString = REFRESH_ENDPOINT) {
                        contentType(ContentType.Application.Json)
                        setBody(buildJsonObject {
                            put("username", storedPreferences.username)
                            put("password", storedPreferences.password)
                        })
                        markAsRefreshTokenRequest()
                    }
                    val refreshedToken = refreshedTokenResponse.body<TokenResponse>()

                    // Save new token
                    preferencesStorage.saveNewAccessToken(
                        refreshedToken.token, refreshedToken.expiration
                    )

                    BearerTokens(refreshedToken.token, null)
                }

                // return false for urls which don't need an Authorization header
                // (ex: token or refresh token urls),
                // otherwise return true to include the Authorization header
                sendWithoutRequest { request ->
                    request.url.buildString() != CREATE_USER_ENDPOINT &&
                            request.url.buildString() != LOGIN_ENDPOINT &&
                            !request.url.buildString().contains("$BASE_URL/images")
                }
            }
        }
    }

    return httpClient
}
