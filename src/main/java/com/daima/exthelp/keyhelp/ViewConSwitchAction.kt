package com.daima.exthelp.keyhelp

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class ViewConSwitchAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project? = event.project
        val currentFile: VirtualFile? = FileEditorManager.getInstance(project!!).selectedFiles.firstOrNull()

        if (currentFile != null) {
            val currentFileName = currentFile.name
            val parentDirectory = currentFile.parent

            // Determine the target file to switch to
            val targetFileName = when {
                currentFileName.endsWith("Vc.js") -> currentFileName.removeSuffix("Vc.js") + ".js"
                currentFileName.endsWith(".js") -> currentFileName.removeSuffix(".js") + "Vc.js"
                else -> return // If not a JS file, do nothing
            }

            // Find the target file in the same directory
            val targetFile = parentDirectory.findChild(targetFileName)

            if (targetFile != null) {
                // Open the target file in editor
                FileEditorManager.getInstance(project).openFile(targetFile, true)
            }
        }
    }
}