package org.oar.ualkt.commands

import org.oar.ualkt.model.InternalConfig
import org.oar.ualkt.model.InternalConfigCommand.EXIT
import org.oar.ualkt.model.InternalConfigCommand.RELOAD
import org.oar.ualkt.model.InternalConfigElement
import org.oar.ualkt.services.controller.Controller
import kotlin.system.exitProcess

class InternalCommand(
    private val config : InternalConfigElement,
) : Command() {

    override val keyWord: String = config.key

    override val title: String = when(config.command) {
        EXIT -> "Exits ualkt"
        RELOAD -> "Reloads ualkt configuration"
    }

    init {
        generateMd5Id()
    }

    override fun perform(argsList: List<String>, controller: Controller) {
        when(config.command) {
            EXIT -> exitProcess(0)
            RELOAD -> controller.reload()
        }
    }

    companion object {
        fun generate(data: InternalConfig): List<InternalCommand> = listOf(
            InternalConfigElement(
                key = data.exit,
                command = EXIT
            ),
            InternalConfigElement(
                key = data.reload,
                command = RELOAD
            )
        ).map(::InternalCommand)
    }
}