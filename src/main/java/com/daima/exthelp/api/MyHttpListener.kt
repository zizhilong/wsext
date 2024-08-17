package com.daima.exthelp.api

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.selectAndHighlightPsiElement
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
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
            // 允许所有来源的跨域请求
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-Type")

            // 如果是 OPTIONS 请求，直接返回 200 响应
            if (exchange.requestMethod.equals("OPTIONS", ignoreCase = true)) {
                exchange.sendResponseHeaders(200, -1)
                return
            }

            // 解析请求中的查询参数
            val queryParams = exchange.requestURI.query?.split("&")
                ?.associate {
                    val (key, value) = it.split("=")
                    key to value
                }

            // 获取 "data" 参数，并进行处理
            val data = queryParams?.get("data")
            val response = if (data != null) {
                processRequestData(data, project)
            } else {
                "Missing 'data' parameter"
            }

            // 发送响应
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }

        /**
         * 处理请求数据，解码并打开指定文件和行号
         */
        private fun processRequestData(data: String, project: Project): String {
            val decodedString = String(Base64.getDecoder().decode(data))
            val parts = decodedString.split("|")

            return if (parts.size == 2) {
                openFileAtLine(project, parts)
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
fun openFileAtLine(project: Project, parts: List<String>) {
    val filePathPart = parts[0].replace("http://localhost:1841/", "").replace(Regex("\\?.*"), "")
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
                    // 查找 PsiFile 并进行跳转
                    PsiManager.getInstance(project).findFile(virtualFile)?.let { psiFile ->
                        navigatePsi(psiFile, result.indices)
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
fun navigatePsi(file: PsiFile, idx: IntArray) {
    if (file !is JSFile) return

    var parser = Parser("SCR{value define}R{value Ext}<<aO")
    var objLiteral: JSObjectLiteralExpression? = parser.Run(file) as? JSObjectLiteralExpression

    var currentIdx = idx
    while (objLiteral != null) {
        val itemsProperty = objLiteral.findProperty("items")
        val itemsArray = itemsProperty?.value as? JSArrayLiteralExpression

        if (itemsArray == null || itemsArray.expressions.size <= currentIdx.first()) {
            selectAndHighlightPsiElement(file.project, objLiteral) // 如果没有子节点或超出索引，则高亮当前对象
            return
        }

        val newObjLiteral = itemsArray.expressions[currentIdx.first()] as? JSObjectLiteralExpression ?: return
        objLiteral = newObjLiteral
        currentIdx = currentIdx.drop(1).toIntArray()

        if (currentIdx.isEmpty()) {
            selectAndHighlightPsiElement(file.project, objLiteral) // 如果索引数组为空，表示已到最后元素，进行高亮
            return
        }
    }
}
