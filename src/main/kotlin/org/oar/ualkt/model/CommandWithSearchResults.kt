package org.oar.ualkt.model

import org.oar.ualkt.commands.Command

data class CommandWithSearchResults(
    val command: Command,
    val searchResults: SearchResult
)
