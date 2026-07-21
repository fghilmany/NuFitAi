package com.fghilmany.nufitai.core.error

/**
 * Sealed interface representing all possible errors in the application.
 * Errors are never thrown across layers — always wrapped in [AppResult].
 */
sealed interface Failure {
    val message: String

    data class Network(override val message: String) : Failure
    data class Database(override val message: String) : Failure
    data class Validation(override val message: String) : Failure
    data class Unauthorized(override val message: String = "Session expired") : Failure
}
