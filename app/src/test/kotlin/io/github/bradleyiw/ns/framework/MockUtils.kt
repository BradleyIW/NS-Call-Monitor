package io.github.bradleyiw.ns.framework

fun <T> buildListOfType(numberOfRows: Int = 5, response: T): List<T> =
    mutableListOf<T>()
        .apply {
            repeat(numberOfRows) { add(response) }
        }.toList()
