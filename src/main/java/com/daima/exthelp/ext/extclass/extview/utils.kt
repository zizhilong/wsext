package com.daima.exthelp.ext.extclass.extview

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement

//用于输入Xtype打双引号
val doubleBracketInsertHandler = InsertHandler<LookupElement> { context, _ ->
    val document = context.document
    val startOffset = context.startOffset
    val tailOffset = context.tailOffset
    // 插入左括号
    document.insertString(startOffset, "\"")
    // 插入右括号
    document.insertString(tailOffset + 1, "\"")
    // 可选: 移动光标到括号之间
    context.editor.caretModel.moveToOffset(tailOffset + 1)
    // 提交更改
    context.commitDocument()
}