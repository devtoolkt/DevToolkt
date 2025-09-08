package dev.toolkt.core.platform

external class FinalizationRegistry(
    cleanupCallback: (heldValue: dynamic) -> Unit,
) {
    /**
     * Registers a value with this FinalizationRegistry so that if the value is garbage-collected, the registry's callback may get called.
     */
    fun register(
        target: dynamic,
        heldValue: dynamic,
        unregisterToken: dynamic = definedExternally,
    )

    /**
     * @return A boolean value that is true if at least one cell was unregistered and false if no cell was unregistered.
     */
    fun unregister(
        unregisterToken: dynamic,
    ): Boolean
}
