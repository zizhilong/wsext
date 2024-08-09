package com.daima.exthelp.jsdebug
import com.intellij.database.util.common.asSingleList
import com.intellij.lang.javascript.service.ui.JSConsoleViewPanel
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.javascript.debugger.console.legacy.JSConsoleView


import com.intellij.xdebugger.impl.XDebugSessionImpl
import java.beans.PropertyChangeListener

class MyDebuggerManagerListener : XDebuggerManagerListener {

    override fun processStarted(debugProcess: XDebugProcess) {
        var a=1
        //val consoleView = debugProcess.session.consoleView.addMessageFilter();
        //val listener = MyDebugProcessListener(consoleView)

        //(debugProcess.session as? XDebugSessionImpl)?.addSessionListener(listener)
    }

    override fun processStopped(debugProcess: XDebugProcess) {
        // 处理调试进程停止时的逻辑
        var a=2
    }

    override fun currentSessionChanged(previousSession: XDebugSession?, currentSession: XDebugSession?) {
        // 处理调试会话变化时的逻辑
        if(currentSession!=null){
            currentSession.runContentDescriptor
            if(currentSession.consoleView is JSConsoleView){
               val jsv= currentSession.consoleView as JSConsoleView
                //与浏览器导航，文件加载，暂停恢复执行有关的一系列事件
                //jsv.debugProcess.connection.addDebugListener(ConnDebug())
                //不知道干啥。没反应
                jsv.addMessageFilter(Filter(jsv))
                //处理断点调试上下文
                //currentSession.currentStackFrame


                //currentSession.runContentDescriptor.attachedContent
                //命令行输入窗口
                //jsv.editorDocument.addDocumentListener(MyDocumentListener())
                //jsv.debugProcess.con

                //UI显示有关的监听
                jsv.addAncestorListener(TestAncestorListener())
                //jsv.conso
                //jsv.addMessageFilter(Filter())
                //jsv.editorDocument
                //jsv.currentEditor
                //jsv.editor
        }
        //
    }
    }
}