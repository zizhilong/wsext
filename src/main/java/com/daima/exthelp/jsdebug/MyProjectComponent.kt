package com.daima.exthelp.jsdebug

import com.intellij.designer.propertyTable.editors.TextEditor
import com.intellij.execution.console.LanguageConsoleBuilder.GutteredLanguageConsole
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.javascript.debugger.console.legacy.JSConsoleView
import com.intellij.javascript.debugger.console.legacy.WebConsole
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.debugger.frame.CallFrameView
import java.io.File
import java.io.FileWriter
import java.io.IOException


class MyProjectComponent: AnAction() {
    val manager = CodeElementsManager()




    @Override
    override fun actionPerformed(event: AnActionEvent) {
        // 获取当前项目






        var sess=XDebuggerManager.getInstance(event.project!!).currentSession
        //当前帧
        sess!!.currentStackFrame

        sess.debugProcess.dropFrameHandler
        //类型为XSuspendContext
        var suspendContext = sess.getSuspendContext()
        ApplicationManager.getApplication().runReadAction {
            printAllStackFramesToFile(sess, event.project!!)
        }


    }
    private fun printAllStackFramesToFile(session: XDebugSession, project: Project) {
        val suspendContext: XSuspendContext? = session.suspendContext
        if (suspendContext != null) {
            val executionStack: XExecutionStack? = suspendContext.activeExecutionStack
            if (executionStack != null) {
                executionStack.computeStackFrames(0, object : XExecutionStack.XStackFrameContainer {
                    override fun addStackFrames(stackFrames: List<XStackFrame>, last: Boolean) {
                        for (frame in stackFrames) {
                            var functionName=""
                            if(frame is CallFrameView){
                                //赋予函数名
                                if(frame.callFrame.functionName!=null){
                                    functionName=frame.callFrame.functionName!!
                                }
                            }

                            val sourcePosition = frame.sourcePosition
                            if (sourcePosition != null) {
                                val psiFile = getPsiFileFromSourcePosition(project, sourcePosition.file)
                                var sourcestr=sourcePosition.file.name

                                if(psiFile?.name=="ext-all-rtl-debug.js" && !manager.isLoad){
                                    manager.Load(psiFile as JSFile)
                                }
                                val extinfo=manager.findElementByLine(sourcePosition.line)
                                if(extinfo!=null){
                                    sourcestr=extinfo.className+"."+extinfo.methodName
                                }
                                println("${sourcestr} Line: ${sourcePosition.line+1} Func: $functionName ")
                            }
                        }
                    }

                    override fun errorOccurred(errorMessage: String) {
                        println("Error occurred: $errorMessage")
                    }
                })
            } else {
                println("No active execution stack.")
            }
        } else {
            println("No suspend context.")
        }
    }
    private fun getPsiFileFromSourcePosition(project: Project, virtualFile: VirtualFile): PsiFile? {
        var ret:PsiFile?=null
        ApplicationManager.getApplication().runReadAction {
            ret=PsiManager.getInstance(project).findFile(virtualFile)
        }
        return ret
    }
}