package org.oar.ualkt.model

// Internal config
data class InternalConfig(
    val exit: String,
    val reload: String
)
data class InternalConfigElement(
    val key: String,
    val command: InternalConfigCommand,
)
enum class InternalConfigCommand {
    EXIT, RELOAD
}

// Firefox config
data class FirefoxConfig(
    val bin: String? = null,
    val profileFolder: String? = null,
    val exclude: List<String> = emptyList()
)
data class FirefoxConfigElement(
    val profile: String,
    val bookmark: FirefoxBookmark
)
