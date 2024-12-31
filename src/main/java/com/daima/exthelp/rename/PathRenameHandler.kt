package com.daima.exthelp.rename

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.daima.exthelp.ext.ns.pathToNamespace
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.util.regex.Pattern

// 专门处理路径重命名的逻辑
class PathRenameHandler {
    fun handlePathRename(directory: PsiDirectory, project: Project) {
        val oldName = directory.name

        // 自定义逻辑，例如弹窗获取新目录名称
        val dialog = CustomRenameDialog(project, oldName)
        if (dialog.showAndGet()) { // 用户点击 OK 按钮
            val newName = dialog.getNewName()
            if (newName.isNotBlank() && newName != oldName) {
                //先返回命名空间映射
                updateNamespace(project, directory, oldName, newName)

                // 确保写操作
                WriteCommandAction.runWriteCommandAction(project) {
                    directory.setName(newName)
                    println("Renamed directory $oldName to $newName")
                }

                // 更新命名空间相关逻辑
            } else {
                println("Rename operation canceled or no changes.")
            }
        }
    }

    private fun updateNamespace(project: Project, directory: PsiDirectory, oldName: String, newName: String) {
        val projectBasePath = project.basePath ?: return

        // 获取改名前的命名空间
        val oldNamespace = pathToNamespace(directory.virtualFile.path.removePrefix("$projectBasePath/"), project)

        // 构建改名后的路径
        val parentPath = directory.virtualFile.parent?.path?.removePrefix("$projectBasePath/") ?: return
        val newDirectoryPath = "$parentPath/$newName"

        // 获取改名后的命名空间
        val newNamespace = pathToNamespace(newDirectoryPath, project)

        // 打印改名前后命名空间
        if (oldNamespace != null && newNamespace != null) {
            println("Namespace updated:")
            println("Old Namespace: $oldNamespace")
            println("New Namespace: $newNamespace")
            updateNamespaceAndReplaceInFiles(project,oldNamespace,newNamespace)
        } else {
            println("Failed to determine namespaces for the directory rename.")
        }
        // TODO: 添加具体的命名空间更新逻辑（如同步其他代码中的引用）
    }

    private fun updateNamespaceAndReplaceInFiles(
        project: Project,
        oldName: String,
        newName: String
    ) {
        val projectBasePath = project.basePath ?: return
        val baseDir = projectBasePath.let { com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(it) }
            ?: return
        val srcVirtualFile = baseDir.findChild("src") ?: return
        val srcDirectory = PsiManager.getInstance(project).findDirectory(srcVirtualFile) ?: return


        // 打印改名前后命名空间
            println("Namespace updated:")
            println("Old Namespace: $oldName")
            println("New Namespace: $newName")

            // 替换 src 目录下所有文件内容
            replaceNamespaceInFiles(srcDirectory, oldName, newName)
    }



    private fun replaceNamespaceInFiles(directory: PsiDirectory, oldNamespace: String, newNamespace: String) {
        val psiManager = PsiManager.getInstance(directory.project)
        val pattern = Pattern.compile("([\"'])$oldNamespace\\.*?([\"'])")

        directory.files.forEach { file ->
            println("file Namespace: $file.name")
            replaceContentInFile(file, pattern, newNamespace)
        }

        directory.subdirectories.forEach { subDir ->
            replaceNamespaceInFiles(subDir, oldNamespace, newNamespace)
        }
    }

    private fun replaceContentInFile(file: PsiFile, pattern: Pattern, newNamespace: String) {
        val document = file.viewProvider.document ?: return

        val oldText = document.text
        val matcher = pattern.matcher(oldText)

        val newText = matcher.replaceAll { matchResult ->
            matchResult.group(1) + newNamespace + matchResult.group(2)
        }
        if(file.name=="Depbedmap.js"){

        }

        if (newText != oldText) {
            val psiDocumentManager = com.intellij.psi.PsiDocumentManager.getInstance(file.project)
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
            document.setText(newText)
            psiDocumentManager.commitDocument(document)

            println("Updated file: ${file.virtualFile.path}")
        }
    }

}
