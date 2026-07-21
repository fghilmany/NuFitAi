package com.fghilmany.nufitai.core.error

/**
 * A discriminated union representing the result of an operation that can fail.
 * Replaces exceptions as the error-passing mechanism across layers.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val failure: Failure) : AppResult<Nothing>
}

/**
 * Folds an [AppResult] into a value of [R] by handling both success and error cases.
 */
inline fun <T, R> AppResult<T>.fold(
    onError: (Failure) -> R,
    onSuccess: (T) -> R,
): R = when (this) {
    is AppResult.Success -> onSuccess(data)
    is AppResult.Error -> onError(failure)
}

/**
 * Returns the data if success, or null if error.
 */
fun <T> AppResult<T>.getOrNull(): T? = fold(
    onError = { null },
    onSuccess = { it },
)

/**
 * Returns the data if success, or throws the failure as an exception.
 * Use only at the boundary (e.g., ViewModel → UI).
 */
fun <T> AppResult<T>.getOrThrow(): T = fold(
    onError = { throw IllegalStateException(it.message) },
    onSuccess = { it },
)
