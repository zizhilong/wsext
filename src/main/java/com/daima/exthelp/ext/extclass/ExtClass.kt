package com.daima.exthelp.ext.extclass

import com.daima.exthelp.ext.extclass.extfunc.ExtFunc
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.ext.log.Info
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSFunctionExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

// 定义 ExtClass 实现 ClassInterface 接口
// 定义 ExtClass 实现 ClassInterface 接口
open class ExtClass(
    private val className: String,
    protected val jsObject: JSObjectLiteralExpression,
    private val parentClass: ClassInterface<Any>? = null, // 可选的父类
    private val functions: List<ExtFunction> = listOf() // 函数列表
) : ClassInterface<Any>, CodeHelpInterface {

    // 获取父类接口实例，可能为空
    override fun getParent(): ClassInterface<Any>? {
        return parentClass
    }

    // 获取当前类名
    override fun getClassName(): String {
        return className
    }
    fun getBaseName(): String {
        var currentClass: ClassInterface<Any>? = this

        while (currentClass?.getParent() != null) {
            currentClass = currentClass.getParent()
        }

        return currentClass?.getClassName() ?: className // 如果没有父类，则返回当前类名
    }

    // 获取函数列表
    override fun getFunctions(): List<ExtFunction> {
        return functions
    }
    // 获取函数列表
    override fun getChildren(ctx: Context,name:String): CodeHelpInterface? {
        var a=1
        return null
    }
    override fun renderPage(){
        //渲染页面。base处理页面中所有的函数
        //渲染Func
        renderFunctions()

        return
    }

    // Optionally, you can implement additional logic to process the class
    override fun toString(): String {
        return "ExtClass(className='$className', jsObject=$jsObject)"
    }
    //获取本对象自有公共函数
    // 获取本对象自有公共函数
    fun renderFunctions() {
        //val blockFuncs = mutableListOf<BlockFunc>()
        // 遍历 jsObject 的属性
        for (property in jsObject.properties) {
            // 获取属性名
            val propertyName = property.name

            // 检查属性值是否为 JSFunctionExpression
            val value = property.value
            if (value is JSFunctionExpression && propertyName != null) {
                ExtFunc(value,fun(): Context {
                    return getContext(propertyName)
                })
            }
        }
    }

    // 获取上下文对象
    open fun getContext(funName: String = ""): Context {
        val context = Context()
        // 将当前对象添加到上下文中，键为 "this"
        context.addClass("this", this)
        return context
    }
    //EXT原生对象不存在PSI，无需冲渲染
    override fun reParse(psi:PsiElement): Boolean {
        return false
    }
    // 用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        Info("getCodeSuggestions")
        var parent=this.parentClass
        if(parent is CodeHelpInterface){
            return parent.getCodeSuggestions(psiElement,context)
        }
        //TODO 应该要返回自身带有的函数
        return listOf()
    }
}
