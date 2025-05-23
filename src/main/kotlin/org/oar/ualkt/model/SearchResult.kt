package org.oar.ualkt.model

data class SearchResult(
    val level: SearchLevel,
    val matchingIndexes: List<Pair<Int, Int>> = emptyList()
)

enum class SearchLevel {
    NOT_FOUND,
    STARTING,
    CONTAINS,
    SPLITTED
}
