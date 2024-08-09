package com.daima.exthelp.jsdebug
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity
import com.intellij.xdebugger.XDebuggerManager

class MyStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        //consoleView = debugProcess.session.consoleView.addMessageFilter();
        //XDebuggerManager.getInstance(e.project!!).currentSession.addSessionListener()
        val connection =project.messageBus.connect()
        //connection.subscribe(XDebuggerManager.TOPIC, MyDebuggerManagerListener())
        connection.subscribe(XDebuggerManager.TOPIC, MyDebuggerManagerListener())
        //println("MyStartupActivity executed for project: ${project.name}")
        // 执行初始化逻辑

    }
}