package com.daima.exthelp.ext.extclass.extfunc

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.ext.log.Info
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.JSEqualElementType
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

//处理赋值的专用类
class ExtVar(
    private val jsVar: JSVarStatement,
    private val jscontext: Context
) : CodeHelpInterface{
    init {

        Info("init")
        //jsVar.putUserData(CODE_HELP_KEY, this)
        fixContext()
    }
    fun fixContext(){
        jsVar.children.forEach { v ->
            //普通属性调用表达 this.xxx.xxxx
            if (v is JSVariable) {
                //如果
                val variableName = v.name ?: return@forEach
                //小于3不考虑,不是等于不考虑
                if(v.children.size!=1){
                    return@forEach
                }
                var cls=getExpressionClass(jscontext,v.children[0])
                if(cls!=null){
                    if(cls is ClassInterface<*>){
                        jscontext.addClass(variableName,cls)
                    }
                }
            }
        }
    }

    override fun reParse(psi: PsiElement): Boolean {
        Info("reParse")


        return true
    }
    // 用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        Info("getCodeSuggestions")
        // 返回空列表作为占位符
        return listOf()
    }


}