package com.daima.exthelp.filetitle


import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager

class FileTreeUpdaterKS(private val project: Project) {
    fun startListening() {
        VirtualFileManager.getInstance().addVirtualFileListener(FileChangeListenerKS(project), project)
    }
}

// 示例：简单文件变动监听器
class FileChangeListenerKS(private val project: Project) : com.intellij.openapi.vfs.VirtualFileListener {
    override fun fileCreated(event: com.intellij.openapi.vfs.VirtualFileEvent) {
        println("File created: ${event.file.name}")
    }

    override fun fileDeleted(event: com.intellij.openapi.vfs.VirtualFileEvent) {
        println("File deleted: ${event.file.name}")
    }

    override fun fileMoved(event: com.intellij.openapi.vfs.VirtualFileMoveEvent) {
        println("File moved: ${event.file.name}")
    }
}