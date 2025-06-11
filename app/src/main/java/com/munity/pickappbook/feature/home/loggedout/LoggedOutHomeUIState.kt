package com.munity.pickappbook.feature.home.loggedout

data class LoggedOutHomeUIState(
    val usernameLogin: String = "",
    val passwordLogin: String = "",
    val usernameCreate: String = "",
    val passwordCreate: String = "",
    val isLoading: Boolean = false,
    val imageByteArray: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoggedOutHomeUIState

        if (isLoading != other.isLoading) return false
        if (usernameLogin != other.usernameLogin) return false
        if (passwordLogin != other.passwordLogin) return false
        if (usernameCreate != other.usernameCreate) return false
        if (passwordCreate != other.passwordCreate) return false
        if (!imageByteArray.contentEquals(other.imageByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + usernameLogin.hashCode()
        result = 31 * result + passwordLogin.hashCode()
        result = 31 * result + usernameCreate.hashCode()
        result = 31 * result + passwordCreate.hashCode()
        result = 31 * result + (imageByteArray?.contentHashCode() ?: 0)
        return result
    }
}