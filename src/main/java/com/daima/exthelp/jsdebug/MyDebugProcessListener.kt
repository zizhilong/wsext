package com.daima.exthelp.jsdebug

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import org.jetbrains.annotations.NotNull

class MyDebugProcessListener(private val consoleView: ConsoleView) : XDebugSessionListener {

    fun processStarted(@NotNull session: XDebugSession) {
        consoleView.print("Debug process started\n", ConsoleViewContentType.SYSTEM_OUTPUT)
    }

    fun processStopped(@NotNull session: XDebugSession) {
        consoleView.print("Debug process stopped\n", ConsoleViewContentType.SYSTEM_OUTPUT)
    }
}