package com.munity.pickappbook.util

object StringsUtil {
    private val regex = Regex(
        pattern = """\b[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\.)+[A-Z]{2,63}\b""",
        option = RegexOption.IGNORE_CASE
    )

    /**
     * @return `true` if [usernameToCheck] is a valid email address
     * */
    fun isValidUsername(usernameToCheck: String): Boolean {
        return regex.matches(usernameToCheck) || usernameToCheck.isBlank()
    }
}
