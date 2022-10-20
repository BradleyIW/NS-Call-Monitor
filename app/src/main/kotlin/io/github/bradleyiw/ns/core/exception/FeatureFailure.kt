package io.github.bradleyiw.ns.core.exception

sealed class Failure
open class FeatureFailure : Failure()
