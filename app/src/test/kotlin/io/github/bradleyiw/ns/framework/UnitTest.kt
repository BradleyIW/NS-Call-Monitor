package io.github.bradleyiw.ns.framework

import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    MockKExtension::class
)
open class UnitTest {
    @Suppress("LeakingThis")
    @Rule
    @JvmField
    val mockkRule = MockKRule(this)
}
