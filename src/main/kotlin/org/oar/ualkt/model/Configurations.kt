package org.oar.ualkt.model

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