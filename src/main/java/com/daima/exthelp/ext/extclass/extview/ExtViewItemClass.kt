package com.daima.exthelp.ext.extclass.extview

import com.daima.exthelp.Exp.PExp.Parser
import com.daima.exthelp.Tools.ExpHelper
import com.daima.exthelp.Tools.StringHelper
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext


// 描述一个View下边的Class并实现 CodeHelp 接口
class ExtViewItemClass(
     val jsObject: JSObjectLiteralExpression,
     val extClassName:String?=null
) : CodeHelpInterface {
    // 存储下级对象的数组
    private val subItems: MutableList<ExtViewItemClass> = mutableListOf()

    init {
        // 初始化时尝试创建下级对象
        initializeSubItems()
    }
    // 初始化子项
    private fun initializeSubItems() {
        jsObject.putUserData(CODE_HELP_KEY, this)
        val itemProperty = getObjProperty(jsObject, "items")

        // 检查 item 属性是否是数组
        if (itemProperty?.value is JSArrayLiteralExpression) {
            val itemArray = itemProperty.value as JSArrayLiteralExpression

            // 遍历数组中的每个元素并创建子项
            itemArray.expressions
                .filterIsInstance<JSObjectLiteralExpression>()
                .forEach { subItems.add(ExtViewItemClass(it)) }
        }
        // 处理事件绑定
        val listeners = getObjProperty(jsObject, "listeners")
        if (listeners?.value is JSObjectLiteralExpression) {
            val listenerObj = listeners.value as JSObjectLiteralExpression
            var xtype = getXtypeName()
            xtype=StringHelper.trimQuotes(xtype)
            ExtViewListeners(listenerObj, xtype)
        }
    }
    // 获取所有子项
    fun getSubItems(): List<ExtViewItemClass> {
        return subItems
    }
    // 实现 CodeHelp 接口中的方法
    override fun reParse(psi:PsiElement):Boolean{
        return false
    }

// 假设 getCodeSuggestions 是用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        ExpHelper.logPsiHierarchy(psiElement)
        //尝试确定是否修改的xtype属性
        val xtypeParser = Parser("LY{name xtype}")
        var ret = xtypeParser.RunExp(psiElement)
        if(ret==null){
            ret=Parser("LRY{name xtype}").RunExp(psiElement)
        }
        // 创建一个存储建议的列表
        val lookupElements = mutableListOf<LookupElementBuilder>()
        //如果是直接改的xtype属性
        if (ret != null) {
            // 遍历 XType 映射并添加到建议列表中
            for ((key, value) in ExtXtypeNameMap().getXtype()) {
                lookupElements.add(
                    LookupElementBuilder.create(key)
                        .withTypeText(value)
                        .withIcon(AllIcons.Nodes.Class)
                        .withInsertHandler(doubleBracketInsertHandler)
                )
            }
            return lookupElements
        }
        //如果不是修改Xtype属性
        val obj = Parser("LREO").RunExp(psiElement)
        //如果属于属性设定
        if(obj!=null){
            //
            val extclass = Load.getExtclassByName(this.getXtypeName()) ?: return listOf()

            return extclass.getLookupElementsByPrefix(this.getPrefix(psiElement),"Config")
        }
        return listOf()
    }
    fun getXtypeName():String{
        //如果有强制设定的类型。则直接使用
        if(this.extClassName!=null){
            return extClassName
        }
        val xtypeProperty=jsObject.findProperty("xtype")
        val classname = xtypeProperty?.value?.text
        if (classname == null) {return ""}
        return StringHelper.trimQuotes(classname)
    }
    fun getPropertyTextValue(key: String): String? {
        val property = jsObject.findProperty(key) ?: return null
        val value = property.value?.text ?: return null
        return StringHelper.trimQuotes(value)
    }



}