package com.example.demo2.InsertHandler.newEvent

import com.example.demo2.Exp.SExp.Parser
import com.example.demo2.Tools.JSObjectLiteralUnit
import com.example.demo2.Tools.StringHelper.trimQuotes
import com.example.demo2.extdata.Events
import com.example.demo2.extdata.Extclass
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import java.io.File
import java.io.IOException


class ViewControllerHelper(private val project: Project, clsName: String) {
    private val filePath: String
    private var vFile: VirtualFile? = null
    private var editor :Editor?=null

    init {
        val result = clsName.substring(clsName.indexOf('.') + 1).replace('.', '/')
        this.filePath = project.basePath + "/classic/src/" + result + ".js"
        openAndCreateFile()
    }

    private fun openAndCreateFile() {
        vFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(this.filePath)
        if (vFile == null) {
            createAndOpenFile(this.filePath, "1111")
        } else {
            openFileInEditor(vFile!!)
        }
    }

    private fun openFileInEditor(virtualFile: VirtualFile) {
        val fileEditors: Array<FileEditor> = FileEditorManager.getInstance(project).openFile(virtualFile, true)
        // 查找文本编辑器并获取Editor对象
        editor = fileEditors
            .filterIsInstance<TextEditor>()
            .firstOrNull()
            ?.editor


    }

    private fun createAndOpenFile(filePath: String, content: String) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                try {
                    val file = File(filePath)
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    val parentVirtualFile =
                        LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file.parentFile)
                    if (parentVirtualFile != null) {
                        val newFile =
                            parentVirtualFile.createChildData(this, file.name)
                        newFile.setBinaryContent(content.toByteArray())
                        openFileInEditor(newFile)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun createEventBind(itemId: String, item: LookupElement) {
        val itemId=trimQuotes(itemId)
        val file = PsiManager.getInstance(project).findFile(vFile!!) ?: return

        var exp = Parser("SCR{value define}R{value Ext}<<aOY{name init}FBSCaO")

        exp.debug = true

        var obj = exp.Run(file) as JSObjectLiteralExpression ?: return

        var objUnit = JSObjectLiteralUnit(obj)
        var itemProp = objUnit.findProperty("#$itemId")
        if (itemProp == null) {
            itemProp = objUnit.addProperty("\'#$itemId\'", "{}", null)
        }

        val eventObj = itemProp.value as JSObjectLiteralExpression? ?: return
        //获得某个对象的事件绑定清单的Obj
        val eventbind = eventObj.findProperty(item.lookupString)
        //如果没找到这个事件,创建事件,如果存在事件绑定就退出好了.可能需要在做个定位
        if (eventbind != null) {
            return;
        }
        objUnit = JSObjectLiteralUnit(eventObj)
        objUnit.addProperty(item.lookupString," this."+itemId+"_"+item.lookupString,null);
        //插入函数
        val funname=itemId+"_"+item.lookupString
         exp = Parser("SCR{value define}R{value Ext}<<aO")
         obj = exp.Run(file) as JSObjectLiteralExpression ?: return
         objUnit=JSObjectLiteralUnit(obj)


        //putUserData

         val evt=item.getUserData(Extclass.EVENTDATA) as Events

        var parastr =""
        var docstr=""
        //生成注释
        docstr ="/**\n"
        docstr+=" * "+evt.html.replace("\n","")+"\n"
        docstr+=" * \n"

        for ( param in evt.params) {
            //param.name
            if(parastr.isNotEmpty()){
                parastr+=", "
            }
            var desc=param.text
            desc=desc.replace("\n","")
            docstr+=" * @param {${param.valueType}} ${param.name} - $desc\n";
            parastr+=param.name;
        }
        docstr+=" */\n"
         var funstr="function ($parastr) {\n"
         funstr+="\t\n"
         funstr+="}\n"
        if(editor==null){
            return
        }
        //先插入新的函数xxxx:function{
         val ret=objUnit.addProperty(funname,funstr,obj.properties[obj.properties.size-1])
        objUnit.commentForProperty(ret,docstr);
        //在这之前在插入注释信息
        val offset: Int = ret.children[0].children[2].children[1].textOffset+2
        val logicalPosition: LogicalPosition = editor!!.offsetToLogicalPosition(offset)
        editor!!.caretModel.moveToLogicalPosition(logicalPosition)
        editor!!.scrollingModel.scrollToCaret(ScrollType.CENTER)

    }
}