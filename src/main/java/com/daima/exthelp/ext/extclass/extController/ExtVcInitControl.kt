package com.daima.exthelp.ext.extclass.extController

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.extclass.extview.ExtViewClass
import com.daima.exthelp.ext.extclass.extview.ExtViewItemClass
import com.daima.exthelp.ext.extclass.extview.ExtViewListeners
import com.daima.exthelp.ext.extclass.extview.ItemSelector
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

class ExtVcInitControl(
    val context: Context,
    val ViewClassName: String,
) : CodeHelpInterface {
    private val extViewListenersList = mutableListOf<ExtViewListeners>()

    override fun reParse(psi: PsiElement): Boolean {
        val view = Pool.findByClassName<Any>(psi.project, ViewClassName)
        if (view !is ExtViewClass) {
            return false
        }
        view.renderPage()
        //专门处理控制器中init: function () { this.control({的部分
        if (psi is JSCallExpression && psi.arguments.size == 1) {
            if (psi.arguments[0] is JSObjectLiteralExpression) {
                val obj = psi.arguments[0] as JSObjectLiteralExpression
                for (p in obj.properties) {
                    if(p.value !is JSObjectLiteralExpression){
                        continue
                    }
                    //根据选择器取得目标的对象
                    val className = getViewItemClassName(view, p.name?:"")
                    if(className!=null){
                        val listener = ExtViewListeners(p.value as JSObjectLiteralExpression, className)
                        extViewListenersList.add(listener)
                    }
                }
            }
        }
        return true
    }
    //获得所有监听器
    fun getExtViewListenersList(): List<ExtViewListeners> {
        return extViewListenersList
    }
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        return listOf()
    }

    private fun getViewItemClassName(view: ExtViewClass, name: String): String? {
        // 解析输入字符串
        val list =ItemSelector(view.extViewItem,name)
        if(list.size>0) {
            return list[0].getXtypeName()
        }else{
            return ""
        }
    }


}