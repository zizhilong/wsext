package com.daima.exthelp.ext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.lang.javascript.psi.JSFile // Import JSFile
import com.intellij.openapi.ui.Messages

class RunAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val project: Project? = event.project
        if (project != null) {
            val fileEditorManager = FileEditorManager.getInstance(project)
            val virtualFile: VirtualFile? = fileEditorManager.selectedFiles.firstOrNull()

            if (virtualFile != null) {
                val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(virtualFile)

                if (psiFile is JSFile) {
                    // The current file is a JavaScript file
                    createExtFileParser(psiFile)
                } else {
                    // Not a JavaScript file, handle accordingly
                    Messages.showMessageDialog(project, "The current file is not a JavaScript file.", "Information", Messages.getInformationIcon())
                }
            }
        }
    }

    private fun createExtFileParser(jsFile: JSFile) {
        //val parser = ParserFile()
        //parser.parseFile(jsFile)
    }
}