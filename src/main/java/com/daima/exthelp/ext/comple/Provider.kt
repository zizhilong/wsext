package com.daima.exthelp.ext.comple
import com.daima.exthelp.ext.comple.mod.Dbm
import com.daima.exthelp.ext.comple.mod.Ext
import com.daima.exthelp.ext.extclass.ParseFile
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.log.Info

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
class Provider : CompletionProvider<CompletionParameters>() {
    // To add Xtype, uncomment the next line and add Xtype() to the list
    // runners = listOf(ClassParam(), Xtype())

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {

        //获得当前指针节点
        val rawpsi: PsiElement = parameters.position
        /*处理DBM*/
        var dbmlist= Dbm().Run(rawpsi)
        if(dbmlist.size>0)
        {
            result.addAllElements(dbmlist)
            return
        }
        var extlist= Ext().Run(rawpsi)
        if(extlist.size>0)
        {
            result.addAllElements(extlist)
            return
        }

        //迭代PSI\
        var element=rawpsi
        //获取当前页面
        //加载当前页面元素表
        val start = System.currentTimeMillis()
        //try {
        //渲染页面
            val psifile=findJSFile(element)
            if(psifile==null){
                return
            }
            ParseFile.setProject(psifile.project)
            val extclass= ParseFile.parseFile(psifile)
            if(extclass==null){
                return
            }
            extclass.renderPage()
                val end = System.currentTimeMillis()
                val elapsedTime = end - start
                println("Elapsed time: $elapsedTime ms")
                //上一次渲染的节点
                var lastParse:PsiElement?=null
                //向前寻找
                 while(element!==null){
                    val codehelp=element.getUserData(CODE_HELP_KEY)
                    //如果存在Code帮助方法则退出
                    if(codehelp!=null){
                        //执行重渲染。重新开始
                        if(lastParse !=element && codehelp.reParse(element)){
                            lastParse=element
                            element=rawpsi
                            continue
                        }
                        Info("Find Codehelp"+codehelp.javaClass.name)
                        result.addAllElements(codehelp.getCodeSuggestions(rawpsi,context))
                        break
                    }
                     //需要实际测试看
                    if(element.prevSibling==null){
                        if(element!=null){
                        element=element.parent
                        }
                    }else{
                        element=element.prevSibling
                    }
                }

            //} catch (e: Exception) {
            //    println("Caught an ArithmeticException: ${e.message}")
        //}
    }
    fun findJSFile(psiElement: PsiElement): JSFile? {
        var currentElement: PsiElement? = psiElement

        // 循环向上查找父节点
        while (currentElement != null) {
            // 检查当前节点是否是 JSFile
            if (currentElement is JSFile) {
                return currentElement
            }
            // 获取父节点
            currentElement = currentElement.parent
        }

        // 如果没有找到 JSFile，则返回 null
        return null
    }
}