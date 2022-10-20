@file:Suppress("NestedBlockDepth", "TooGenericExceptionCaught")

package io.github.bradleyiw.ns.core.database

import android.database.sqlite.SQLiteException
import io.github.bradleyiw.ns.core.exception.Either
import io.ktor.util.reflect.*

open class DatabaseService {
    suspend fun <T> oneShotDbRequest(localRequest: suspend () -> T?): Either<DatabaseFailure, T> =
        try {
            localRequest()?.let {
                if (it.instanceOf(List::class)) {
                    val response = it as List<*>
                    if (response.isEmpty()) {
                        Either.Left(NoResultsError)
                    } else {
                        Either.Right(it)
                    }
                } else {
                    Either.Right(it)
                }
            } ?: Either.Left(NoResultsError)
        } catch (e: IllegalStateException) {
            Either.Left(DatabaseStateError(e))
        } catch (e: SQLiteException) {
            Either.Left(SQLError(e))
        } catch (e: Exception) {
            Either.Left(GenericDatabaseError(e))
        }
}
