package org.oar.ualkt.commands

import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.model.SearchResult
import org.oar.ualkt.services.controller.Controller
import java.security.MessageDigest

abstract class Command {
    lateinit var id: String

    abstract val keyWord: String
    open val title = "- no title -"
    open val icon = "ualkt"

    var fixedPriority: Int? = null
    var keepHistory = true
    var requiresParams = false
    var caseInsensitive = false
    var startsWith = true

    fun generateMd5Id() {
        this.id = MessageDigest.getInstance("MD5")
            .digest((this::class.simpleName+this.keyWord).toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    abstract fun perform(argsList: List<String>, controller: Controller)

    open fun match(inputText: String): SearchResult {
        if (inputText.isBlank()) {
            return SearchResult(SearchLevel.NOT_FOUND)
        }

        var value = inputText

        if (this.requiresParams) {
            val split = inputText.split(' ');
            if (split.size > 1 && this.keyWord != split[0]) {
                return SearchResult(SearchLevel.NOT_FOUND)
            }
            value = split[0];
        }
        if (this.startsWith) {
            val searchResult = search(this.keyWord, value, this.caseInsensitive, false, this.title === this.keyWord);
            return if (searchResult.level === SearchLevel.STARTING) searchResult
            else SearchResult(SearchLevel.NOT_FOUND)
        }

        return search(this.keyWord, value, true, true, this.title === this.keyWord)
    }

    protected fun search(
        originalText: String,
        searchText: String,
        caseInsensitive: Boolean = false,
        split: Boolean = true,
        recordIndexes: Boolean = true
    ): SearchResult {

        if (searchText.isEmpty()) return SearchResult(SearchLevel.NOT_FOUND)

        val (text, search) = if (caseInsensitive) {
            originalText.lowercase() to searchText.lowercase()
        } else {
            originalText to searchText
        }

        val index = text.indexOf(search)
        if (index >= 0) {
            return SearchResult(
                level = if (index == 0) SearchLevel.STARTING else SearchLevel.CONTAINS,
                matchingIndexes = if (recordIndexes) listOf(index to index + search.length) else emptyList()
            )
        }

        var lastIndexChecked = 0
        var currentLevel = SearchLevel.STARTING
        val foundIndexes = mutableListOf<Int>()

        val success = search
            .mapIndexed { i, letter -> i to letter }
            .all { (i, letter) ->
                val foundIdx = text.substring(lastIndexChecked).indexOf(letter)

                when {
                    foundIdx == 0 && (currentLevel == SearchLevel.STARTING || currentLevel == SearchLevel.CONTAINS) -> {
                        foundIndexes.add(lastIndexChecked)
                        lastIndexChecked++
                    }
                    foundIdx > 0 && i == 0 -> {
                        foundIndexes.add(lastIndexChecked + foundIdx)
                        lastIndexChecked = foundIdx + 1
                        currentLevel = SearchLevel.CONTAINS
                    }
                    foundIdx >= 0 && split -> {
                        foundIndexes.add(lastIndexChecked + foundIdx)
                        lastIndexChecked += foundIdx + 1
                        currentLevel = SearchLevel.SPLITTED
                    }
                    else -> return@all false // break
                }
                true
            }

        if (success) {
            val matchingIndexes = if (!recordIndexes) {
                emptyList()
            } else {
                foundIndexes.fold(mutableListOf<Pair<Int, Int>>()) { acc, idx ->
                    if (acc.isEmpty()) {
                        acc.add(idx to idx + 1)
                    } else {
                        val lastIndexes = acc.last()
                        if (lastIndexes.second == idx) {
                            acc[acc.lastIndex] = lastIndexes.first to idx + 1
                        } else {
                            acc.add(idx to idx + 1)
                        }
                    }
                    acc
                }
            }
            return SearchResult(level = currentLevel, matchingIndexes = matchingIndexes)
        }

        return SearchResult(SearchLevel.NOT_FOUND)
    }
}