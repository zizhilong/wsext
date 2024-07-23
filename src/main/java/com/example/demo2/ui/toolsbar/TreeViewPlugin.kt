package com.example.demo2.ui.toolsbar
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.content.ContentFactory
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.nio.file.Paths
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel
import java.awt.Desktop
import java.net.URI
import javax.swing.tree.DefaultMutableTreeNode

class TreeViewPlugin : ToolWindowFactory, ProjectComponent {

    private lateinit var tree: JTree
    private lateinit var treeModel: DefaultTreeModel
    private lateinit var searchField: JTextField
    //private lateinit var webServer: StaticWebServer
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // 启动静态 Web 服务
        val resourceBase = Paths.get(project.basePath ?: "").toAbsolutePath().toString()
        /*
        webServer = StaticWebServer(9810, resourceBase)
        webServer.start()
         */
        // 创建面板
        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()

        // 创建搜索文本框并设置最大大小
        searchField = JTextField()
        searchField.preferredSize = Dimension(200, 24)
        searchField.maximumSize = Dimension(Int.MAX_VALUE, 24)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.weightx = 1.0
        panel.add(searchField, constraints)

        // 创建树
        tree = JTree()
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        val root = TreeNode("Root", "Root Node")
        treeModel = DefaultTreeModel(root)
        tree.model = treeModel

        // 将树添加到滚动窗格中
        val treeScrollPane = JScrollPane(tree)
        constraints.fill = GridBagConstraints.BOTH
        constraints.gridx = 0
        constraints.gridy = 1
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        panel.add(treeScrollPane, constraints)

        // 将面板内容添加到工具窗口
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        // 监听文件打开事件
        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                    updateTreeContent(file, project)
                }

                override fun selectionChanged(event: FileEditorManagerEvent) {
                    val selectedFile = event.newFile
                    if (selectedFile != null) {
                        updateTreeContent(selectedFile, project)
                    }
                }
            }
        )

        // 监听文件编辑事件
        val editorManager = FileEditorManager.getInstance(project)
        editorManager.selectedTextEditor?.document?.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val file = FileDocumentManager.getInstance().getFile(event.document)
                if (file != null) {
                    updateTreeContent(file, project)
                }
            }
        })
    }

    private fun updateTreeContent(file: VirtualFile, project: Project) {
        // 获取 PSI 文件
        val psiFile = PsiManager.getInstance(project).findFile(file)

        // 判断是否为 JavaScript 文件


        if (psiFile == null || psiFile !is JSFile) {
            /*
            val filePath = file.path.replace(project.basePath ?: "", "").replace("\\", "/")
            openInBrowser("http://localhost:8080/$filePath")
            return
             */
            return
        }
        //Text.GetNode
        var test =Test()
        var node = test.GetNode(psiFile)
        val root = treeModel.root as TreeNode
        root.add(node)
        treeModel.reload()

    }
    private fun openInBrowser(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        }
    }


}