package org.oar.ualkt.services.controller

import org.oar.ualkt.commands.Command
import org.oar.ualkt.model.ConfigFileStructure
import org.oar.ualkt.model.InternalConfig
import org.oar.ualkt.model.SearchLevel
import org.oar.ualkt.services.command.CommandLoader
import org.oar.ualkt.ui.MainUI

class Controller(
    private val ui: MainUI
) {
    private var commands = CommandLoader.load(ConfigFileStructure(InternalConfig("exit", "reload")))
    private var filteredCommands = emptyList<Command>()
    private var selectedIndex = 0

    fun reload() {
        commands = CommandLoader.load(ConfigFileStructure(InternalConfig("exit", "reload")))
        filteredCommands = emptyList()
        selectedIndex = 0
    }

    fun onEscape() {
        ui.hideWindow()
    }

    fun onEnter(text: String): Boolean {
        if (filteredCommands.isEmpty()) return false
        filteredCommands[selectedIndex].perform(text.split(" "), this)
        return true
    }

    fun onNext() {
        if (selectedIndex >= filteredCommands.size - 1) return
        selectedIndex++
        ui.changeSelection(selectedIndex - 1, selectedIndex)
    }

    fun onPrev() {
        if (selectedIndex <= 0) return
        selectedIndex--
        ui.changeSelection(selectedIndex + 1, selectedIndex)
    }

    fun onChangedInput(text: String) {
        filteredCommands = commands
            .filter { it.match(text).level != SearchLevel.NOT_FOUND }
        selectedIndex = 0
        ui.replaceOptions(filteredCommands, selectedIndex)
    }
}