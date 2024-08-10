package com.daima.exthelp.ext.extclass

import com.daima.exthelp.ext.interfaces.ClassInterface
import com.intellij.lang.javascript.psi.JSFunctionExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression

// 定义 ExtClass 实现 ClassInterface 接口
// 定义 ExtClass 实现 ClassInterface 接口
open class ExtClass(
    private val className: String,
    protected val jsObject: JSObjectLiteralExpression,
    private val parentClass: ClassInterface<Any>? = null, // 可选的父类
    private val functions: List<ExtFunction> = listOf() // 函数列表
) : ClassInterface<Any> {

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
    override fun renderPage(){
        return
    }

    // Optionally, you can implement additional logic to process the class
    override fun toString(): String {
        return "ExtClass(className='$className', jsObject=$jsObject)"
    }

    //获取本对象自有公共函数
    // 获取本对象自有公共函数
    fun getThisPublicFunctions(): List<BlockFunc> {
        val blockFuncs = mutableListOf<BlockFunc>()

        // 遍历 jsObject 的属性
        for (property in jsObject.properties) {
            // 获取属性名
            val propertyName = property.name

            // 检查属性值是否为 JSFunctionExpression
            val value = property.value
            if (value is JSFunctionExpression && propertyName != null) {
                // 创建 BlockFunc 对象
                val blockFunc = BlockFunc(this.getContext(),propertyName, value)
                blockFuncs.add(blockFunc)
            }
        }

        return blockFuncs
    }
    // 获取上下文对象
    fun getContext(): Context<Any> {
        val context = Context<Any>()
        // 将当前对象添加到上下文中，键为 "this"
        context.addClass("this", this)
        return context
    }
}
