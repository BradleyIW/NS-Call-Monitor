package io.github.bradleyiw.ns.core.usecase

import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class OneShotUseCase<in Params, out Type> {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    suspend fun execute(params: Params): Either<Failure, Type> =
        withContext(Dispatchers.IO) {
            run(params)
        }
}
