package io.github.bradleyiw.ns.core.usecase

import kotlinx.coroutines.flow.Flow

interface ReactiveUseCase<in Params, out Type> {

    fun run(params: Params): Flow<Type>

    fun execute(params: Params): Flow<Type> {
        return run(params)
    }
}
