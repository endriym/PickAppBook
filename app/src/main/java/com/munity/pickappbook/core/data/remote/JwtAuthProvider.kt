package com.munity.pickappbook.core.data.remote

import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.auth.AuthProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.utils.io.KtorDsl

/**
 * Installs the client's [JwtAuthProvider].
 *
 */
public fun AuthConfig.jwt(block: JwtAuthConfig.() -> Unit) {
    with(JwtAuthConfig().apply(block)) {
        val bearerAuthProvider =
            BearerAuthProvider(refreshTokens, loadTokens, sendWithoutRequest, realm)
        this@jwt.providers.add(JwtAuthProvider(bearerAuthProvider, realm))
    }
}

/**
 * A configuration for [JwtAuthProvider].
 *
 */
@KtorDsl
public class JwtAuthConfig {
    internal var refreshTokens: suspend RefreshTokensParams.() -> BearerTokens? = { null }
    internal var loadTokens: suspend () -> BearerTokens? = { null }
    internal var sendWithoutRequest: (HttpRequestBuilder) -> Boolean = { true }

    public var realm: String? = null

    /**
     * Configures a callback that refreshes a token when the 401 status code is received.
     *
     */
    public fun refreshTokens(block: suspend RefreshTokensParams.() -> BearerTokens?) {
        refreshTokens = block
    }

    /**
     * Configures a callback that loads a cached token from a local storage.
     * Note: Using the same client instance here to make a request will result in a deadlock.
     *
     */
    public fun loadTokens(block: suspend () -> BearerTokens?) {
        loadTokens = block
    }

    /**
     * Sends credentials without waiting for [io.ktor.http.HttpStatusCode.Unauthorized].
     *
     */
    public fun sendWithoutRequest(block: (HttpRequestBuilder) -> Boolean) {
        sendWithoutRequest = block
    }
}

/**
 * An authentication provider for the JWT HTTP authentication scheme.
 * JWT authentication involves security tokens called bearer tokens.
 * As an example, these tokens can be used as a part of OAuth flow to authorize users of your application
 * by using external providers, such as Google, Facebook, Twitter, and so on.
 *
 */
public class JwtAuthProvider(
    private val bearerAuthProvider: BearerAuthProvider,
    private val realm: String?,
) : AuthProvider by bearerAuthProvider {
    companion object {
        private const val JWT_AUTH_SCHEME = "JWT"
    }

    /**
     * Checks if current provider is applicable to the request.
     *
     */
    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth.authScheme != JWT_AUTH_SCHEME) {
//            LOGGER.trace("JWT Auth Provider is not applicable for $auth")
            return false
        }
        val isSameRealm = when {
            realm == null -> true
            auth !is HttpAuthHeader.Parameterized -> false
            else -> auth.parameter("realm") == realm
        }
//        if (!isSameRealm) {
//            LOGGER.trace("JWT Auth Provider is not applicable for this realm")
//        }
        return isSameRealm
    }
}
