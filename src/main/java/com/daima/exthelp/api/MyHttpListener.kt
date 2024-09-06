package com.daima.exthelp.api

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.selectAndHighlightPsiElement
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSRecursiveElementVisitor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.awt.Window
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.InetSocketAddress
import java.util.*

/**
 * 自定义 HTTP 监听器类，用于在项目打开时启动 HTTP 服务器。
 */
class MyHttpListener(private val project: Project) : ProjectComponent {

    // HTTP 服务器实例
    private var server: HttpServer? = null
    private val log = Logger.getInstance(MyHttpListener::class.java)

    /**
     * 项目打开时触发的事件
     */
    override fun projectOpened() {
        // 如果项目名称不是 "client_extjs"，则不启动服务器
        if (project.name != "client_extjs") return

        try {
            // 创建并启动 HTTP 服务器，监听端口 38085
            server = HttpServer.create(InetSocketAddress(38085), 0).apply {
                createContext("/open", MyHandler(project)) // 设置处理请求的上下文
                executor = null // 使用默认的执行器
                start()
            }
            log.info("HTTP server started on port 38085")
        } catch (e: IOException) {
            log.error("Failed to start HTTP server", e) // 启动失败时记录错误日志
        }
    }

    /**
     * 项目关闭时触发的事件
     */
    override fun projectClosed() {
        server?.stop(0) // 停止 HTTP 服务器
        log.info("HTTP server stopped")
    }

    /**
     * 处理 HTTP 请求的处理器类
     */
    class MyHandler(private val project: Project) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            // 允许跨域
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            exchange.responseHeaders.add("Access-Control-Allow-Headers", "*")

            if (exchange.requestMethod.equals("OPTIONS", ignoreCase = true)) {
                exchange.sendResponseHeaders(200, -1)
                return
            }

            // 解析查询参数
            val queryParams = exchange.requestURI.query?.split("&")
                ?.associate {
                    val (key, value) = it.split("=")
                    key to value
                }

            val data = queryParams?.get("data")
            val paraname = queryParams?.get("paraname") ?: ""
            val paravalue = queryParams?.get("paravalue") ?: ""

            val response = if (data != null) {
                processRequestData(data, project, paraname, paravalue)
            } else {
                "Missing 'data' parameter"
            }

            // 返回响应
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }

        private fun processRequestData(data: String, project: Project, paraname: String, paravalue: String): String {
            val decodedString = String(Base64.getDecoder().decode(data))
            val parts = decodedString.split("|")

            return if (parts.size == 2) {
                openFileAtLine(project, parts, paraname, paravalue)
                "Decoded data: $decodedString"
            } else {
                "Invalid data format"
            }
        }
    }
}

/**
 * 解析行号或索引字符串的结果类型
 */
sealed class LineOrPsiIndexResult {
    data class LineNumber(val line: Int) : LineOrPsiIndexResult()
    data class PsiIndex(val indices: IntArray) : LineOrPsiIndexResult()
}

/**
 * 解析行号或索引字符串
 * 规则:
 * 1. 如果参数包含 "index"，则返回 PsiIndex 类型，用于查找 PSI 元素。
 * 2. 如果参数不包含 "index"，则返回 LineNumber 类型，用于打开文件后选择特定行。
 *
 * @param part 字符串参数，可能包含行号或 "index" 前缀的索引信息
 * @return LineOrPsiIndexResult 的一个实例，包含行号或 PSI 索引信息
 */
fun parseLineNumberOrIdx(part: String): LineOrPsiIndexResult {
    return when {
        part.startsWith("index") -> {
            // 如果 part 以 "index" 开头，则移除前缀 "index" 并将剩余部分解析为整数数组，返回 PsiIndex 类型
            val indices = part.removePrefix("index").split(',').map { it.toInt() }.toIntArray()
            LineOrPsiIndexResult.PsiIndex(indices)
        }
        part.all { it.isDigit() } -> {
            // 如果 part 是纯数字字符串，则将其作为行号返回，返回 LineNumber 类型
            LineOrPsiIndexResult.LineNumber(part.toInt())
        }
        else -> {
            // 其他情况暂不处理，返回一个默认的 LineNumber 类型
            LineOrPsiIndexResult.LineNumber(0)
        }
    }
}

/**
 * 打开指定的文件并定位到指定的行号或索引
 */
fun openFileAtLine(project: Project, parts: List<String>, paraname: String, paravalue: String) {
    val filePathPart = parts[0]
        .replace("http://localhost:1841/", "")
        .replace("http://localhost:3000/", "")
        .replace("http://localhost:3001/", "")
        .replace(Regex("\\?.*"), "")

    val lineNumberOrIdx = parts[1]

    // 解析行号或索引
    val result = parseLineNumberOrIdx(lineNumberOrIdx)
    val fullPath = "${project.basePath ?: return}/$filePathPart"

    val file = File(fullPath)
    if (!file.exists()) {
        println("File not found: $fullPath")
        return
    }

    // 查找虚拟文件并打开
    LocalFileSystem.getInstance().findFileByIoFile(file)?.let { virtualFile ->
        ApplicationManager.getApplication().invokeLater {
            when (result) {
                is LineOrPsiIndexResult.LineNumber -> {
                    val openFileDescriptor = OpenFileDescriptor(project, virtualFile, result.line, 0)
                    FileEditorManager.getInstance(project).openEditor(openFileDescriptor, true)
                    bringEditorToFront(project) // 将编辑器置于前台
                }
                is LineOrPsiIndexResult.PsiIndex -> {
                    // 查找 PsiFile 并进行跳转，传递 paraname 和 paravalue
                    PsiManager.getInstance(project).findFile(virtualFile)?.let { psiFile ->
                        navigatePsi(psiFile, result.indices, paraname, paravalue)
                    } ?: println("PsiFile not found: ${file.path}")
                }
            }
        }
    } ?: println("Virtual file not found: $fullPath")
}
/**
 * 将编辑器窗口置于前台
 */
fun bringEditorToFront(project: Project) {
    EditorFactory.getInstance().allEditors.firstOrNull()?.let { editor ->
        IdeFocusManager.getInstance(project).requestFocus(editor.contentComponent, true)
    }

    WindowManager.getInstance().suggestParentWindow(project)?.apply {
        toFront()
        requestFocus()
    }
}

/**
 * 根据索引数组导航到指定的 Psi 元素并高亮显示
 */
/**
 * 根据提供的索引数组在 PsiFile 中导航到特定的 JSObjectLiteralExpression 元素，
 * 并调用 selectAndHighlightPsiElement 函数来高亮或处理该元素。
 *
 * @param file PsiFile 对象，必须是 JSFile 类型，代表要解析的文件
 * @param idx IntArray，包含要导航的索引数组，每个索引代表 JSON 树中一个嵌套的对象
 * @param paraname String，额外的参数，表示属性名称，用于在 selectAndHighlightPsiElement 中使用
 * @param paravalue String，额外的参数，表示属性值，用于在 selectAndHighlightPsiElement 中使用
 */
fun navigatePsi(file: PsiFile, idx: IntArray, paraname: String, paravalue: String) {
    // 确保文件类型是 JSFile，否则直接返回
    if (file !is JSFile) return

    // 使用自定义的 Parser 来解析文件中的 JSObjectLiteralExpression
    var parser = Parser("SCR{value define}R{value Ext}<<aO")
    var objLiteral: JSObjectLiteralExpression? = parser.Run(file) as? JSObjectLiteralExpression

    // 当前的索引数组，用于在 JSON 树中逐层导航
    var currentIdx = idx

    // 如果 paraname 不为空，开始遍历整个文件寻找匹配的对象
    if (paraname.isNotEmpty()) {
        val matchingElement = findMatchingPsiElement(file, paraname, paravalue)
        if (matchingElement != null) {
            // 如果找到匹配的 Psi 元素，则直接高亮显示
            selectAndHighlightPsiElement(file.project, matchingElement)
            return
        }
    }

    // 循环遍历对象树中的 items 属性，直到找到目标对象或用尽索引
    while (objLiteral != null) {
        // 获取当前对象的 "items" 属性
        val itemsProperty = objLiteral.findProperty("items")

        // 尝试将 "items" 属性解析为数组，如果失败，则结束循环
        val itemsArray = itemsProperty?.value as? JSArrayLiteralExpression

        // 如果 itemsArray 为空，或者数组的大小小于当前索引值，表示无法继续导航
        if (itemsArray == null || itemsArray.expressions.size <= currentIdx.first()) {
            // 到达目标节点，调用 selectAndHighlightPsiElement 处理或高亮该对象
            selectAndHighlightPsiElement(file.project, objLiteral)
            return
        }

        // 使用当前索引值从数组中获取下一个 JSObjectLiteralExpression 对象
        val newObjLiteral = itemsArray.expressions[currentIdx.first()] as? JSObjectLiteralExpression ?: return

        // 将当前对象更新为获取到的新对象，继续下一轮循环
        objLiteral = newObjLiteral

        // 移除已经处理过的索引，继续处理下一个嵌套层级
        currentIdx = currentIdx.drop(1).toIntArray()

        // 如果索引数组为空，表示已经到达目标对象，进行处理或高亮
        if (currentIdx.isEmpty()) {
            selectAndHighlightPsiElement(file.project, objLiteral)
            return
        }
    }
    /**
     * 遍历 PsiFile，查找带有特定属性名和属性值的 JSObjectLiteralExpression。
     *
     * @param file PsiFile 对象
     * @param paraname 属性名称
     * @param paravalue 属性值
     * @return 找到的 JSObjectLiteralExpression 对象，如果没有找到则返回 null
     */

}
fun findMatchingPsiElement(file: PsiFile, paraname: String, paravalue: String): JSObjectLiteralExpression? {
    // 遍历 PsiFile 中的所有元素
    val visitor = object : JSRecursiveElementVisitor() {
        var foundElement: JSObjectLiteralExpression? = null

        override fun visitJSObjectLiteralExpression(expression: JSObjectLiteralExpression) {
            // 检查对象是否包含属性 paraname
            val property = expression.findProperty(paraname)
            if (property != null && property.value != null) {
                // 获取属性值并去除可能的单引号
                val valueText = property.value?.text?.removeSurrounding("'")?.removeSurrounding("\"")

                // 检查属性值是否等于 paravalue
                if (valueText == paravalue) {
                    foundElement = expression
                }
            }
            // 继续递归遍历
            super.visitJSObjectLiteralExpression(expression)
        }
    }

    // 遍历 PsiFile
    file.accept(visitor)

    // 返回找到的元素
    return visitor.foundElement
}