package com.mjs

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.event.*
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.awt.event.KeyEvent

/**
 * 监听编辑器的创建和释放事件的类。
 * 当一个新的编辑器被创建时，注册鼠标监听器和拖动监听器。
 */
class MyEditorFactoryListener : EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val project = editor.project ?: return

        // 注册鼠标点击和拖动事件监听器
        val mouseListener = MyEditorMouseListener(project, editor)
        editor.addEditorMouseListener(mouseListener)

        val mouseMotionListener = MyEditorMouseMotionListener(mouseListener)
        editor.addEditorMouseMotionListener(mouseMotionListener)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        // 当编辑器关闭时，执行清理操作（如果有需要）
    }
}

/**
 * 监听鼠标点击、拖动的自定义类。
 * 在编辑器内处理用户的鼠标事件，记录拖动的起始文件和拖动结束后的目标文件。
 */
class MyEditorMouseListener(val project: Project, val editor: Editor) : EditorMouseListener {

    // 起始文件的虚拟路径
    private var startFile: VirtualFile? = null
    // 标记是否处于拖动状态
    var isDragging = false
    // 被拖动的函数名称
    private var draggedFunctionName: String? = null
    // 是否按住 Ctrl 键
    private var isCtrlPressed = false

    /**
     * 当鼠标按下时，记录起始文件路径并获取拖动的函数名称。
     */
    override fun mousePressed(event: EditorMouseEvent) {
        // 初始化拖动标志
        isDragging = false

        // 检查是否按住了 Ctrl 键
        isCtrlPressed = isCtrlKeyPressed(event)

        // 获取当前编辑器文档的文件路径
        startFile = FileDocumentManager.getInstance().getFile(editor.document)

        // 获取当前光标位置的 PSI 元素，确认是否为导出函数
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return
        val element = psiFile.findElementAt(editor.caretModel.offset)

        // 如果选中元素是导出函数，则记录函数名
        element?.let {
            draggedFunctionName = getFunctionNameIfExported(it)
        }
    }

    /**
     * 当鼠标释放时，检查是否为跨文件拖动，并在目标文件中插入 import 语句。
     */
    override fun mouseReleased(event: EditorMouseEvent) {
        // 如果按住了 Ctrl 键且拖动了函数，并且起始文件路径和函数名有效
        if (isDragging  && startFile != null && draggedFunctionName != null) {
            // 获取目标文件的路径
            val targetFile = FileDocumentManager.getInstance().getFile(editor.document)

            // 处理跨文件拖动，插入 import 语句
            targetFile?.let {
                handleCrossFileDrag(it)
            }

            // 阻止默认的粘贴操作
            event.consume()
        }
    }

    /**
     * 处理跨文件拖动事件，生成项目根路径下的绝对路径，并插入 import 语句。
     */
    private fun handleCrossFileDrag(targetFile: VirtualFile) {
        startFile?.let { sourceFile ->
            // 获取项目根路径
            val projectBasePath = project.basePath ?: return

            // 获取目标文件的相对路径，去除项目根路径部分
            val targetPath = targetFile.path
            val relativePath = if (targetPath.startsWith(projectBasePath)) {
                targetPath.substring(projectBasePath.length) // 去掉项目根路径前缀
            } else {
                targetPath
            }

            // 构造 import 语句（使用项目内的绝对路径）
            val importStatement = "import { $draggedFunctionName } from \"$relativePath\";"
            // 在目标文件中插入 import 语句
            insertImportStatement(project, editor, importStatement)
        }
    }

    /**
     * 判断当前 PSI 元素是否为导出的函数，并返回函数名。
     */
    private fun getFunctionNameIfExported(element: PsiElement): String? {
        // 判断是否是导出函数，返回函数名
        return if (element.text.startsWith("export")) {
            PsiTreeUtil.getParentOfType(element, PsiElement::class.java)?.text
        } else {
            null
        }
    }

    /**
     * 在目标文件的编辑器中插入给定的 import 语句到鼠标当前的位置。
     */
    private fun insertImportStatement(project: Project, editor: Editor, importStatement: String) {
        // 使用 WriteCommandAction 确保插入操作的原子性
        WriteCommandAction.runWriteCommandAction(project) {
            val document = editor.document
            // 获取当前光标的位置
            val offset = editor.caretModel.offset
            // 在当前光标位置插入 import 语句
            document.insertString(offset, "$importStatement\n")
        }
    }

    /**
     * 判断是否按住了 Ctrl 键。
     */
    private fun isCtrlKeyPressed(event: EditorMouseEvent): Boolean {
        val ctrlMask = KeyEvent.CTRL_DOWN_MASK
        return (event.mouseEvent.modifiersEx and ctrlMask) != 0
    }
}

/**
 * 监听鼠标拖动事件的自定义类。
 * 当发生拖动时，设置拖动状态为 true。
 */
class MyEditorMouseMotionListener(val mouseListener: MyEditorMouseListener) : EditorMouseMotionListener {

    /**
     * 当检测到鼠标拖动时，标记拖动状态为 true。
     */
    override fun mouseDragged(event: EditorMouseEvent) {
        // 标记当前操作为拖动
        mouseListener.isDragging = true
    }
}
