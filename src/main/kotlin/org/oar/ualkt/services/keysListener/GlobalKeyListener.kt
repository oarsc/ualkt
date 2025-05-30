package org.oar.ualkt.services.keysListener

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener

class GlobalKeyListener(
    private val callback: () -> Unit
) : NativeKeyListener {
    private var altPressed: Boolean = false

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        if (e.keyCode == NativeKeyEvent.VC_ALT) {
            altPressed = true
        }

        if (altPressed && e.keyCode == NativeKeyEvent.VC_M) {
            callback()
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        if (e.keyCode == NativeKeyEvent.VC_ALT) {
            altPressed = false
        }
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {
        // Not needed
    }

    companion object {
        fun register(callback: () -> Unit) {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(GlobalKeyListener(callback))
        }
    }
}