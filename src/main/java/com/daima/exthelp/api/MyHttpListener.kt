package com.daima.exthelp.api

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.selectAndHighlightPsiElement
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
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


class MyHttpListener(private val project: Project) : ProjectComponent {

    private var server: HttpServer? = null
    private val log = Logger.getInstance(MyHttpListener::class.java)

    override fun projectOpened() {
        try {
            server = HttpServer.create(InetSocketAddress(38085), 0).apply {
                createContext("/open", MyHandler(project))
                executor = null // creates a default executor
                start()
            }
            log.info("HTTP server started on port 38085")
        } catch (e: IOException) {
            log.error("Failed to start HTTP server", e)
        }
    }

    override fun projectClosed() {
        server?.stop(0)
        log.info("HTTP server stopped")
    }

    class MyHandler(private val project: Project) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val queryParams = exchange.requestURI.query?.split("&")
                ?.associate {
                    val (key, value) = it.split("=")
                    key to value
                }

            val data = queryParams?.get("data")
            val response = if (data != null) {
                val decodedBytes = Base64.getDecoder().decode(data)
                val decodedString = String(decodedBytes)
                val parts = decodedString.split("|")
                if (parts.size == 2) {
                    openFileAtLine(project, parts)
                    "Decoded data: $decodedString"
                } else {
                    "Invalid data format"
                }
            } else {
                "Missing 'data' parameter"
            }

            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { os: OutputStream ->
                os.write(response.toByteArray())
            }
        }
    }
}

fun openFileAtLine(project: Project, parts: List<String>) {
    val pathPart = parts[0]
    var lineNumber = 0
    var Idx = intArrayOf()

    val part1 = parts[1]

    if (part1.all { it.isDigit() }) {
        // 如果 parts[1] 是纯数字字符串，则转换为数字并赋值给 lineNumber
        lineNumber = part1.toInt()
    } else if (part1.startsWith("index")) {
        // 如果 parts[1] 以 "index" 开头
        val numbers = part1.removePrefix("index")
            .split(',')
            .map { it.toInt() }
            .toIntArray()
        Idx = numbers
    }



    val filePath = pathPart.replace("http://localhost:1841/", "").replace(Regex("\\?.*"), "")

    val projectBasePath = project.basePath ?: return

    val fullPath = "$projectBasePath/$filePath"

    val file = File(fullPath)
    if (!file.exists()) {
        println("文件不存在: $fullPath")
        return
    }

    val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
    if (virtualFile != null) {
        ApplicationManager.getApplication().invokeLater {
            val fileEditorManager = FileEditorManager.getInstance(project)
            val openFileDescriptor = OpenFileDescriptor(project, virtualFile, lineNumber + 1, 0)
            fileEditorManager.openEditor(openFileDescriptor, true)
            //log.info("File opened: $fullPath at line $lineNumber")
            //用于窗口获得焦点
            val editor: Editor = EditorFactory.getInstance().getAllEditors().get(0)
            IdeFocusManager.getInstance(project).requestFocus(editor.getContentComponent(), true)
            // 获取窗口并将其移动到最前端
            val window: Window? = WindowManager.getInstance().suggestParentWindow(project)
            if (window != null) {
                window.toFront()
                window.requestFocus()
            }
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            if (psiFile != null) {
                // 在这里处理 PsiFile
                jumpPsi(psiFile,Idx)
                println("PsiFile: $psiFile")
            } else {
                println("无法找到 PsiFile: ${file.path}")
            }
        }

    } else {
        println("无法找到虚拟文件: $fullPath")
    }
}
fun jumpPsi(file:PsiFile,Idx:IntArray){
    if(file !is JSFile){
        return
    }

        var exp = Parser("SCR{value define}R{value Ext}<<aO")
        var idx=Idx
        //第一个节点
        var extobj=exp.Run(file) as JSObjectLiteralExpression

        while(extobj!=null){
            val items=extobj.findProperty("items")
            if(items==null || items.value !is JSArrayLiteralExpression){
                return
            }
                var arr=items.value as JSArrayLiteralExpression
                //数组长度不够
                if(arr.expressions.size<idx[0]+1){
                    return
                }
                val newobj=arr.expressions[idx[0]]
                if(newobj !is JSObjectLiteralExpression){
                    return
                }
                extobj=newobj
                idx=idx.copyOfRange(1, idx.size)
                //如果数组空了，说明到最后元素了
                if(idx.size==0){
                    selectAndHighlightPsiElement(extobj.project,extobj)
                    return
                }

        }
    return
}