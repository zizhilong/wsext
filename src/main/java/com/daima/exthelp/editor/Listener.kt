package com.daima.exthelp.editor

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.psi.PsiDocumentManager

class Listener: EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val project = editor.project ?: return
        val document = editor.document

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)

        document.addDocumentListener(object : DocumentListener {
            override fun beforeDocumentChange(event: DocumentEvent) {
                // 文档内容变化前执行的操作
            }

            override fun documentChanged(event: DocumentEvent) {
                // 文档内容变化后执行的操作
                val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
                if (psiFile != null) {
                    println("Document content changed: ${psiFile.virtualFile.path}")
                }
            }
        })
    }

}