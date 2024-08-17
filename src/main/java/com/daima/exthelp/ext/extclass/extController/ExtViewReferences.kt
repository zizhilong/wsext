package com.daima.exthelp.ext.extclass.extController

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.extclass.ExtFunction
import com.daima.exthelp.ext.extclass.extview.ItemSelector
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Extclass.Companion.PROPERTIES_DATA
import com.daima.exthelp.extdata.Extclass.Companion.TYPE
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

//一种代码
class ExtViewReferences(
    private val extViewController: ExtViewController,
) : ClassInterface<Any>, CodeHelpInterface {


    // 获取父类接口实例，可能为空
    override fun getParent(): ClassInterface<Any>? {
        return null
    }

    // 获取当前类名
    override fun getClassName(): String {
        return ""
    }

    // 获取函数列表
    override fun getFunctions(): List<ExtFunction> {
        return listOf()
    }
    // 获取函数列表
    override fun getChildren(ctx: Context, name:String): CodeHelpInterface? { return null
    }
    override fun reParse(psiElement: PsiElement):Boolean{
        return true
    }
    // 返回代码提示的列表
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder>{
        val lookupElements = mutableListOf<LookupElementBuilder>()
        val view = extViewController.getView()
        if(view==null){
            return listOf()
        }
        view.renderPage()
        var reflist=ItemSelector(view.extViewItem,"[reference=*]")
        reflist?.forEach{
            item->
            var refname=item.getPropertyTextValue("reference")
            if(refname!=null){
                val builder = LookupElementBuilder.create(refname)
                    .withTypeText(refname) // 设置类型文本为"Config Property"
                    .withIcon(AllIcons.General.Web) // 设置图标
                lookupElements.add(builder)
            }
        }
        return lookupElements
    }
    // 获取父类，返回类型为 ClassInterface<T>? 表示可能为空

    // 获取当前类名，返回 String

    // 获取函数数组
    //获得一个子集元素

    //需要对当前页面进行PSI渲染
    override fun renderPage(){

    }

}