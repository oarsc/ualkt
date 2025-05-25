package org.oar.ualkt.services.controller

import org.oar.ualkt.model.CommandWithSearchResults
import org.oar.ualkt.model.ConfigFileStructure
import org.oar.ualkt.model.FirefoxConfig
import org.oar.ualkt.model.InternalConfig
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.services.command.CommandLoader
import org.oar.ualkt.ui.MainUI

class Controller(
    private val ui: MainUI
) {
    val dummyConfig = ConfigFileStructure(
        InternalConfig("exit", "reload"),
        FirefoxConfig(
            bin = "/usr/bin/firefox-dev",
            exclude = listOf(
                "socks",
                "job",
                "default-release"
            )
        )
    )

    private var commands = CommandLoader.load(dummyConfig)
    private var filteredCommands = emptyList<CommandWithSearchResults>()
    private var selectedIndex = 0

    fun reload() {
        commands = CommandLoader.load(dummyConfig)
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

    fun onChangedInput(text: String, remove: Boolean) {
        filteredCommands = commands
            .map { CommandWithSearchResults(it, it.match(text)) }
            .filter { it.searchResults.level != SearchLevel.NOT_FOUND }

        selectedIndex = 0
        ui.replaceOptions(filteredCommands, selectedIndex)
        if (!remove && filteredCommands.isNotEmpty()) {
            ui.updatePlaceholder(selectedIndex)
        }
    }
}