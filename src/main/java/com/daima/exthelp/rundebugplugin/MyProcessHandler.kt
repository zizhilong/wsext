package com.daima.exthelp.rundebugplugin

import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import java.nio.charset.StandardCharsets


class MyProcessHandler(process: Process) : KillableProcessHandler(process, "MyProcessHandler", StandardCharsets.UTF_8) {

    init {
        ProcessTerminatedListener.attach(this)
    }
}