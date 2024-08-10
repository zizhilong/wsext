package com.daima.exthelp.ext.extclass.extview

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.extclass.ExtClass
import com.daima.exthelp.ext.extclass.ExtFunction
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression

// 继承 ExtClass
class ExtViewClass(
    className: String,
    jsObject: JSObjectLiteralExpression,
    parentClass: ClassInterface<Any>? = null, // 可选的父类
    functions: List<ExtFunction> = listOf()   // 函数列表
) : ExtClass(className, jsObject, parentClass, functions) {
    public var extViewItem: ExtViewItemClass? = null
    //渲染当前view
    override fun renderPage() {
        extViewItem = ExtViewItemClass(jsObject)
    }
    // 添加特定于 ExtViewClass 的属性或方法
    fun GetjsObject():JSObjectLiteralExpression {
        return jsObject
    }
}