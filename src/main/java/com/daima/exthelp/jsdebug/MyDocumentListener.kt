package com.daima.exthelp.jsdebug

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project

class MyDocumentListener : DocumentListener {
    override fun documentChanged(event: DocumentEvent) {
        val newText = event.newFragment.toString()
        println("New console content: $newText")
        // 在这里处理新添加的控制台内容
    }
}

fun addDocumentListenerToConsole(consoleView: ConsoleView, project: Project) {
    val editor = (consoleView as? ConsoleViewImpl)?.editor
    val document = editor?.document
    document?.addDocumentListener(MyDocumentListener())
}