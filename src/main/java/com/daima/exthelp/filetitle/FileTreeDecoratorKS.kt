package com.daima.exthelp.filetitle

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.SimpleTextAttributes
import java.io.File
import java.nio.charset.StandardCharsets

class FileTreeDecoratorKS : ProjectViewNodeDecorator {

    // 缓存目录描述映射
    private val directoryDescriptions: MutableMap<String, String> = mutableMapOf()

    override fun decorate(node: ProjectViewNode<*>, data: com.intellij.ide.projectView.PresentationData) {
        val project: Project? = node.project
        val virtualFile = node.virtualFile ?: return

        // 确保目录描述已加载
        project?.let { loadDescriptions(it) }

        if (virtualFile.isDirectory) {
            val relativePath = getRelativePath(project, virtualFile)
            val description = directoryDescriptions[relativePath] ?: ""
            data.addText(virtualFile.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
            if (description.isNotEmpty()) {
                data.addText(" $description", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        } else if (virtualFile.extension == "mjs" || virtualFile.extension == "js") {
            // 仅限 src/ 目录下的文件
            val relativePath = getRelativePath(project, virtualFile)
            if (relativePath.startsWith("src/")) {
                val annotation = getTopComment(virtualFile) ?: ""
                data.addText(virtualFile.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                data.addText(" $annotation", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
    }

    // 加载目录描述映射，支持多层目录
    private fun loadDescriptions(project: Project) {
        if (directoryDescriptions.isNotEmpty()) return // 已加载则不重复加载

        val docsFile = File("${project.basePath}/docs/dirmemo.md")
        if (docsFile.exists() && docsFile.isFile) {
            docsFile.forEachLine { line ->
                val parts = line.split(" ", limit = 2)
                if (parts.size == 2) {
                    val relativePath = parts[0].trim()
                    val description = parts[1].trim()
                    directoryDescriptions[relativePath] = description
                }
            }
        }
    }

    // 获取文件顶部注释（第一行或以 // 开头的行）
    private fun getTopComment(virtualFile: VirtualFile): String? {
        return try {
            virtualFile.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                reader.lineSequence()
                    .take(5) // 只检查前5行
                    .firstOrNull { it.trim().startsWith("//") } // 找到第一条注释
                    ?.removePrefix("//") // 去除注释标记
                    ?.trim() // 去除多余空格
            }
        } catch (e: Exception) {
            null
        }
    }

    // 获取相对路径
    private fun getRelativePath(project: Project?, virtualFile: VirtualFile): String {
        val projectPath = project?.basePath ?: return virtualFile.name
        return virtualFile.path.removePrefix(projectPath).trimStart('/')
    }
}
