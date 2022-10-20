package io.github.bradleyiw.ns.core.exception

import io.github.bradleyiw.ns.core.exception.Either.Left
import io.github.bradleyiw.ns.core.exception.Either.Right

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    /**
     * Returns true if this is a Right, false otherwise.
     * @see Right
     */
    val isRight get() = this is Right<R>

    /**
     * Returns true if this is a Left, false otherwise.
     * @see Left
     */
    val isLeft get() = this is Left<L>

    /**
     * Creates a Left type.
     * @see Left
     */
    fun <L> left(a: L) = Left(a)


    /**
     * Creates a Right type.
     * @see Right
     */
    fun <R> right(b: R) = Right(b)
}

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
suspend fun <A, B, C> (suspend (A) -> B).c(f: suspend (B) -> C): suspend (A) -> C = {
    f(this(it))
}

/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
suspend fun <T, L, R> Either<L, R>.flatMap(fn: suspend (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Left -> Left(a)
        is Right -> fn(b)
    }

/**
 * Right-biased map() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
suspend fun <T, L, R> Either<L, R>.map(fn: suspend (R) -> (T)): Either<L, T> =
    this.flatMap(fn.c(::right))


/**
 * Left-biased flatMap() FP convention which means that Left is assumed to be the default case
 * to operate on. If it is Right, operations like mapLeft, flatMapLeft, ... return the Right value unchanged.
 */
suspend fun <T, L, R> Either<L, R>.flatMapLeft(fn: suspend (L) -> Either<T, R>): Either<T, R> =
    when (this) {
        is Left -> fn(a)
        is Right -> Right(b)
    }

/**
 * Left-biased map() FP convention which means that Left is assumed to be the default case
 * to operate on. If it is Right, operations like mapLeft, flatMapLeft, ... return the Right value unchanged.
 */
suspend fun <T, L, R> Either<L, R>.mapLeft(fn: suspend (L) -> (T)): Either<T, R> =
    this.flatMapLeft(fn.c(::left))


/** Returns the value from this `Right` or the given argument if this is a `Left`.
 *  Right(12).getOrElse(17) RETURNS 12 and Left(12).getOrElse(17) RETURNS 17
 */
fun <L, R> Either<L, R>.getOrElse(value: R): R =
    when (this) {
        is Left -> value
        is Right -> b
    }

/**
 * Left-biased onFailure() FP convention dictates that when this class is Left, it'll perform
 * the onFailure functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
suspend fun <L, R> Either<L, R>.onFailure(fn: suspend (failure: L) -> Unit): Either<L, R> =
    this.apply { if (this is Left) fn(a) }

/**
 * Right-biased onSuccess() FP convention dictates that when this class is Right, it'll perform
 * the onSuccess functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
suspend fun <L, R> Either<L, R>.onSuccess(fn: suspend (success: R) -> Unit): Either<L, R> =
    this.apply { if (this is Right) fn(b) }


suspend fun <L, R, T> Either<L, R>.foldSuspendable(
    fnL: suspend (L) -> T?,
    fnR: suspend (R) -> T?
): T? =
    when (this) {
        is Left -> fnL(a)
        is Right -> fnR(b)
    }

fun <L, R, T> Either<L, R>.fold(fnL: (L) -> T?, fnR: (R) -> T?): T? =
    when (this) {
        is Left -> fnL(a)
        is Right -> fnR(b)
    }
