package io.github.bradleyiw.ns.framework

import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.fold
import org.junit.jupiter.api.Assertions.fail

fun <L, R> Either<L, R>.assertLeft(leftAssertion: (L) -> Unit) =
    this.fold({ leftAssertion(it) }) { fail("Expected a Left value but got Right") }!!

fun <L, R> Either<L, R>.assertRight(rightAssertion: (R) -> Unit) =
    this.fold({ fail("Expected a Right value but got Left") }) { rightAssertion(it) }!!

fun <L> Either<L, Unit>.assertRight() = assertRight { }
