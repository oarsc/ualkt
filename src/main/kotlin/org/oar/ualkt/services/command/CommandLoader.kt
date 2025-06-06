package org.oar.ualkt.services.command

import org.oar.ualkt.commands.Command
import org.oar.ualkt.commands.FirefoxCommand
import org.oar.ualkt.commands.InternalCommand
import org.oar.ualkt.model.ConfigFileStructure

object CommandLoader {
    val commandsConfig = listOf<(ConfigFileStructure) -> List<Command>>(
        { InternalCommand.generate(it.internal) },
        { FirefoxCommand.generate(it.firefox) }
    )

    fun load(config: ConfigFileStructure)= commandsConfig.flatMap { it(config) }
}