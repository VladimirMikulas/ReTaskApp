package com.vlamik.core.commons

import kotlin.coroutines.cancellation.CancellationException

inline fun <reified E : Throwable, T> Result<T>.onFailureOrRethrow(action: (Throwable) -> Unit): Result<T> {
    return onFailure { if (it is E) throw it else action(it) }
}

inline fun <T> Result<T>.onFailureIgnoreCancellation(action: (Throwable) -> Unit): Result<T> {
    return onFailureOrRethrow<CancellationException, T>(action)
}
