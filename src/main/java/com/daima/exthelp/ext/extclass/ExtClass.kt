package com.daima.exthelp.ext.extclass

import com.intellij.lang.javascript.psi.JSObjectLiteralExpression

// 定义 ExtClass 实现 ClassInterface 接口
// 定义 ExtClass 实现 ClassInterface 接口
class ExtClass(
    private val className: String,
    private val jsObject: JSObjectLiteralExpression,
    private val parentClass: ClassInterface<Any>? = null, // 可选的父类
    private val functions: List<ExtFunction> = listOf() // 函数列表
) : ClassInterface<Any> {
    init{

    }
    // 获取父类接口实例，可能为空
    override fun getParent(): ClassInterface<Any>? {
        return parentClass
    }

    // 获取当前类名
    override fun getClassName(): String {
        return className
    }

    // 获取函数列表
    override fun getFunctions(): List<ExtFunction> {
        return functions
    }

    // Optionally, you can implement additional logic to process the class
    override fun toString(): String {
        return "ExtClass(className='$className', jsObject=$jsObject)"
    }
}
