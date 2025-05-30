package org.oar.ualkt.services.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.model.ConfigFileStructure
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.services.command.CommandLoader
import org.oar.ualkt.ui.MainUI
import java.io.File

class Controller(
    private val ui: MainUI
) {
    private var commands = CommandLoader.load(loadConfig())
    private var filteredCommands = emptyList<CommandWithSearchResults>()
    private var selectedIndex = 0

    fun reload() {
        commands = CommandLoader.load(loadConfig())
        filteredCommands = emptyList()
        selectedIndex = 0
    }

    fun onEscape() {
        ui.hideWindow()
    }

    fun onEnter(text: String): Boolean {
        if (filteredCommands.isEmpty()) return false
        filteredCommands[selectedIndex].command.perform(text.split(" "), this)
        return true
    }

    fun onNext() {
        if (selectedIndex >= filteredCommands.size - 1) return
        selectedIndex++
        ui.changeSelection(selectedIndex - 1, selectedIndex)
        ui.updatePlaceholder(selectedIndex)
    }

    fun onPrev() {
        if (selectedIndex <= 0) return
        selectedIndex--
        ui.changeSelection(selectedIndex + 1, selectedIndex)
        ui.updatePlaceholder(selectedIndex)
    }

    fun resolve(text: String): String? {

        return null
    }

    fun onChangedInput(text: String, remove: Boolean) {
        filteredCommands = commands
            .map { CommandWithSearchResults(it, it.match(text)) }
            .filter { it.searchResults.level != SearchLevel.NOT_FOUND }
            .sortCommands()

        selectedIndex = 0
        ui.replaceOptions(filteredCommands, selectedIndex)
        if (!remove && filteredCommands.isNotEmpty()) {
            ui.updatePlaceholder(selectedIndex)
        }
    }

    private fun loadConfig(): ConfigFileStructure {
        val file = File(System.getProperty("user.home"), ".ualktrc")
        val configJson = file.readText()
        return jacksonObjectMapper().readValue(configJson, ConfigFileStructure::class.java)
    }

    private fun List<CommandWithSearchResults>.sortCommands(): List<CommandWithSearchResults> =
        sortedWith { it1, it2 ->
            val result1 = it1.searchResults
            val result2 = it2.searchResults

            if (result1.level == result2.level) {
                val priority1 = it1.priority
                val priority2 = it2.priority

                when {
                    priority1 != 0 && priority2 != 0 -> priority2 - priority1
                    priority1 != 0 -> -1
                    priority2 != 0 -> 1
                    else -> {
                        val firstMatch1 = result1.matchingIndexes
                        val firstMatch2 = result2.matchingIndexes

                        if (firstMatch1.isNotEmpty() && firstMatch2.isNotEmpty()) {
                            firstMatch1[0].first - firstMatch2[0].first
                        } else {
                            0
                        }
                    }
                }
            }
            else (result1.level.ordinal - result2.level.ordinal)
        }
}