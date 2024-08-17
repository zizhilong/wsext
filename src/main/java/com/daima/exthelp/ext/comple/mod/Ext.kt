package com.daima.exthelp.ext.comple.mod

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import java.sql.Connection
import java.sql.DriverManager

// 继承自 modbase 基类
class Ext : modbase() {


    // 手动指定的二级类目和名称的 Map
    private val TableClassMap: MutableMap<String, String> = mutableMapOf(
        "merge" to "合并对象，右边的object有的key替换左边",
        "applyIf" to "给对象补全默认配置",
        "apply" to "复制配置项到对象中（带默认值）",
        "iterate" to "带作用域迭代器( object, fn, [scope] )",
        "each" to "遍历可迭代类型( array, fn, [scope], [reverse] )",
        "bind" to "绑定函数参数和作用域 ( fn, [scope], [args], [appendArgs] )",
        "callback" to "调用函数(callback, [scope],[args], [delay], [caller],[defaultScope] ):",
        "clone" to "对象克隆",
        "copy" to "复制指定的属性到对象中( dest, source, names, [usePrototypeKeys] ) : Object ",
        "copyIf" to "复制指定的属性到对象中（如果目标中不存在）( dest, source, names) : Object ",
        "now()" to "当前时间戳",
        "toArray" to "可迭代类型转为数组( iterable, [start], [end] ) : Array ",
        "urlAppend" to "URL字符串追加内容( url, string ) : String ",
        "valueFrom" to "( value, defaultValue, [allowBlank] )",
        "define" to "定义类",
        "create" to "创建对象"
    )
    // 从数据库加载数据的方法

    fun Run(psi: PsiElement): List<LookupElement> {

        val continuousText = getContinuousText(psi)

        // 判断是否是以 "Dbm" 开头
        if (!continuousText.startsWith("Ext")) {
            return listOf()
        }
        // 将 continuousText 变成数组
        val parts = continuousText.split('.').map { it.trim() }
        // 判断数组长度为1时，执行 tableclass
        return when (parts.size) {
            1 -> tableclass()
            else -> listOf()
        }
    }

    // 生成基于 TableClassMap 的代码提示元素
    private fun tableclass(): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()
        // 遍历 TableClassMap，生成 LookupElementBuilder
        for ((key, value) in TableClassMap) {
            var lookupElement = LookupElementBuilder.create(key)
                .withTypeText(value) // 将二级类目名称作为类型文本显示
                .withIcon(AllIcons.Nodes.Function) // 设置图标
                //.withInsertHandler(DotInsertHandler) // 设置 InsertHandler
            lookupList.add(PrioritizedLookupElement.withPriority(lookupElement,1000000000.0))
        }

        return lookupList
    }

}