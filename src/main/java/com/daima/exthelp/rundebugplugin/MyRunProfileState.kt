package com.daima.exthelp.rundebugplugin

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.filters.TextConsoleBuilderImpl
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiManager


class MyRunProfileState(private val environment: ExecutionEnvironment) : RunProfileState {
    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult? {
        val project = environment.project
        val consoleView = TextConsoleBuilderImpl(project).console

        // 获取当前活动的编辑器
        val editor: Editor = EditorFactory.getInstance().getAllEditors().get(0) // 获取第一个活动编辑器

                if (editor != null) {
                    // 获取 PsiFile
                    val virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument())
                    if (virtualFile != null) {
                        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                        if (psiFile != null) {
                            consoleView.print("Current file: " + psiFile.name + "\n", ConsoleViewContentType.NORMAL_OUTPUT)
                        } else {
                            consoleView.print("PsiFile not found.\n", ConsoleViewContentType.ERROR_OUTPUT)
                        }
                    } else {
                        consoleView.print("VirtualFile not found.\n", ConsoleViewContentType.ERROR_OUTPUT)
                    }
                } else {
                    consoleView.print("No active editor found.\n", ConsoleViewContentType.ERROR_OUTPUT)
                }

        consoleView.print("Hello, world!\n", ConsoleViewContentType.NORMAL_OUTPUT)
        //return DefaultExecutionResult(consoleView, MyProcessHandler())
        return null
    }
}