package com.daima.exthelp.jsdebug

import com.intellij.openapi.util.NlsContexts
import org.jetbrains.debugger.DebugEventListener
import org.jetbrains.debugger.Script
import org.jetbrains.debugger.SuspendContext
import org.jetbrains.debugger.Vm

class ConnDebug: DebugEventListener {
    override fun suspended(context: SuspendContext<*>) {
    }

    /**
     * Reports the virtual machine has resumed. This can happen
     * asynchronously, due to a user action in the browser (without explicitly resuming the VM through
     */
    override fun resumed(vm: Vm) {
    }

    /**
     * Reports that a new script has been loaded.
     */
    override fun scriptAdded(vm: Vm, script: Script, sourceMapUrl: String?) {
    }

    /**
     * Reports that the script has been collected and is no longer used in VM.
     */
    override fun scriptRemoved(script: Script) {
    }

    override fun scriptsCleared() {
    }

    /**
     * Reports that script source has been altered in remote VM.
     */
    override fun scriptContentChanged(newScript: Script) {
    }

    /**
     * Reports a navigation event on the target.
     *
     * @param newUrl the new URL of the debugged target
     */
    override fun navigated(newUrl: String?) {
    }

    override fun errorOccurred(errorMessage: @NlsContexts.NotificationContent String) {
    }

    override fun childVmAdded(childVm: Vm) {
    }
}