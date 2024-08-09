package com.daima.exthelp.jsdebug

import com.daima.exthelp.Tools.StringHelper.trimQuotes
import com.intellij.database.dialects.base.generator.producers.trimQuery
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

data class CodeElement(
    val className: String,
    val methodName: String,
    val startLine: Int,
    val endLine: Int
)

class CodeElementsManager {
    private val elements = mutableListOf<CodeElement>()
    var isLoad=false;
    fun addElement(element: CodeElement) {
        elements.add(element)
    }

    fun findElementByLine(line: Int): CodeElement? {
        for (element in elements) {
            if (line in element.startLine..element.endLine) {
                return element
            }
        }
        return null
    }
    fun Load(psiFile: JSFile){
        isLoad=true

        ApplicationManager.getApplication().runReadAction {
            //psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
                //override fun visitElement(element: PsiElement) {

                    val document = PsiDocumentManager.getInstance(psiFile.project).getDocument(psiFile)
                    //println("子项目数"+psiFile.children.size)
                    for (c in psiFile.children) {
                        if (c is JSExpressionStatement) {
                            for (c1 in c.children) {
                                if (c1 is JSCallExpression && c1.argumentList != null) {
                                    ExtDefine(c1, document!!)
                                }
                                //如果是直接函数定义
                                if (c1 is JSAssignmentExpression) {
                                   Assig(c1, document!!)
                                }
                            }
                        }
                    }
                    //单独处理base
            psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    super.visitElement(element)
                    if (element is JSCallExpression) {
                        var m = element.methodExpression as? JSReferenceExpression
                        var functionName = m?.referenceName
                        var scope = m?.qualifier?.text ?: ""
                        if (functionName == "addMembers" && scope == "Base") {

                            IntoObject("Base", element.arguments[0] as JSObjectLiteralExpression, document!!)
                        }
                        if(functionName=="apply" && scope=="Ext" &&element.arguments[0].text=="Ext" && element.arguments[1] is JSObjectLiteralExpression){
                            //return
                            IntoObject("Ext",element.arguments[1] as JSObjectLiteralExpression,document!!)
                        }
                        //val functionName = methodExpression.referenceName
                        //val scope = methodExpression.qualifier?.text ?: ""
                        //val functionName = element.methodExpression.name
                        //val scope = element.methodExpression.qualifier?.text ?: ""
                    }
                    if (element is JSFunction) {
                        if (element.name == "makeCtor") {
                            val slineNumber = document!!.getLineNumber(element.textRange.startOffset) + 1
                            val elineNumber = document!!.getLineNumber(element.textRange.endOffset) + 1
                            //println("函数"+extprop.name+":"+slineNumber+":"+elineNumber)
                            addElement(CodeElement("Opera浏览器兼容处理", "makeCtor", slineNumber, elineNumber))
                        }
                    }
                    if(element is JSProperty){
                        //elements
                        //fireEventArgs
                        if(element.name=="fireEventArgs"){
                            //事件触发

                            IntoObject("Ext.mixin.Observable",element.parent as JSObjectLiteralExpression,document!!)
                        }
                    }
                    //特定函数处理
                    if (element is JSFunctionExpression) {
                        //使用var创建函数可以使用
                        if(element.name=="createApp"){
                            val slineNumber = document!!.getLineNumber(element.textRange.startOffset) + 1
                            val elineNumber = document!!.getLineNumber(element.textRange.endOffset) + 1
                            //println("函数"+extprop.name+":"+slineNumber+":"+elineNumber)
                            addElement(CodeElement("ExtApp创建实例", "makeCtor", slineNumber, elineNumber))
                        }
                    }
                        }
                    })
                }
            //})
        //}
    }
    private fun IntoObject(classname:String,obj:JSObjectLiteralExpression,document: Document){

        if(classname=="Ext.Component"){
            val a=1;
        }
        //具体类定义
        for (extprop in obj.properties){
            //传递私有函数
            if((extprop.name=="privates" || extprop.name=="statics") && extprop.value is JSObjectLiteralExpression){
                IntoObject(classname,extprop.value as JSObjectLiteralExpression,document)
            }

            if(extprop.value is JSFunctionExpression){

                val slineNumber = document.getLineNumber(extprop.textRange.startOffset)+1
                val elineNumber = document.getLineNumber(extprop.textRange.endOffset)+1
                //println("函数"+extprop.name+":"+slineNumber+":"+elineNumber)
                addElement(CodeElement(classname, extprop.name!!, slineNumber, elineNumber))
            }
            val a=1
        }
    }

    //直接定义型
    fun Assig(assipsi : JSAssignmentExpression, document: Document){
        val classname=assipsi.lOperand?.text?: ""
        if(classname==""){
            return
        }
        if(classname=="Ext.application")
        {
            var a=1;
        }
         if(assipsi.rOperand is JSObjectLiteralExpression){
             IntoObject(classname,assipsi.rOperand as JSObjectLiteralExpression,document)
         }
    }
    fun ExtDefine(callpsi : JSCallExpression, document: Document){
        val methodExpression = callpsi.methodExpression
        if (methodExpression is JSReferenceExpression) {
            //判定是否为函数定义
            val functionName = methodExpression.referenceName
            val scope = methodExpression.qualifier?.text ?: ""
            //对BASE单独处理
            if(functionName=="addMembers"||scope!="Base"){
                val a=1
                //if(callpsi.arguments[0] is JSObjectLiteralExpression){
                    //IntoObject("Base",callpsi.arguments[0] as JSObjectLiteralExpression,document)
                //}
            }
            //自赋值

            if(functionName!="define" ||scope!="Ext"){
                return
            }
            var clsname=callpsi.arguments[0].text
            if(clsname==null){
                return
            }
            clsname= trimQuotes(clsname)
            if(clsname=="Ext.data.request.Ajax"){
                val a="debug"
            }
            if(callpsi.arguments[1] is JSObjectLiteralExpression){
                var extobj=callpsi.arguments[1] as JSObjectLiteralExpression
                IntoObject(clsname,extobj,document)
                //extobj.children
            }
            //println(clsname)
            //'Ext.data.request.Ajax'
        }
    }
}

