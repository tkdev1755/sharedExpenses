package com.mds.sharedexpenses.data.utils

sealed class DataResult<out T> {
    /**
     * Represents a successful outcome with the fetched data.
     * @param data The data of type T that was successfully retrieved.
     */
    data class Success<out T>(val data: T) : DataResult<T>()

    /**
     * Represents a failure.
     * @param errorCode A specific code representing the type of error (e.g., "PERMISSION_DENIED").
     * @param errorMessage A user-friendly or developer-friendly message explaining the error.
     */
    data class Error(val errorCode: String, val errorMessage: String?) : DataResult<Nothing>()

    /**
     * Represents the state where data was not found at the given reference,
     * which might not be a technical "error" but is a specific failure case.
     */
    object NotFound : DataResult<Nothing>()
}
