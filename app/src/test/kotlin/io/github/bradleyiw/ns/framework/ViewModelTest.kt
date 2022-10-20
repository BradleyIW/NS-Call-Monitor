package io.github.bradleyiw.ns.framework

import io.github.bradleyiw.ns.framework.coroutines.CoroutinesTestExtension
import io.github.bradleyiw.ns.framework.livedata.InstantExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith


@ExperimentalCoroutinesApi
@ExtendWith(
    CoroutinesTestExtension::class,
    InstantExecutorExtension::class
)
open class ViewModelTest : UnitTest()
