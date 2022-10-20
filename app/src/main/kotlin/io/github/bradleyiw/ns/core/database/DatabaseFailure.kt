package io.github.bradleyiw.ns.core.database

import io.github.bradleyiw.ns.core.exception.FeatureFailure

sealed class DatabaseFailure : FeatureFailure()

data class DatabaseStateError(val exception: Exception) : DatabaseFailure()
data class SQLError(val exception: Exception) : DatabaseFailure()
data class GenericDatabaseError(val exception: Exception) : DatabaseFailure()
object NoResultsError : DatabaseFailure()
