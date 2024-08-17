package com.daima.exthelp.ext.extclass

import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

// 定义一个用于描述 Ext. 开头基类信息的类
class ExtBaseClass(
    private val className: String
) : ClassInterface<Any>, CodeHelpInterface {

    // 定义函数列表，初始为空
    private var functions: List<ExtFunction>? = null

    // 获取父类接口实例，可能为空
    override fun getParent(): ClassInterface<Any>? {
        return null
    }

    // 获取当前类名
    override fun getClassName(): String {
        return className
    }
    //需要对当前页面进行PSI渲染
    override fun renderPage(){
        return
    }
    override fun getChildren(ctx:Context,name:String): CodeHelpInterface? {
        val extclass = Load.getExtclassByName(className)
        if(extclass!=null){
            //返回
            var p=extclass.getPropertiesData(name)
            //取得属性，同时只有一种返回值类型
            if(p!=null && p.valueType!=null && p.valueType.size==1){
                var a=1
                return null
            }
        }
        return null
    }
    // 获取函数列表，延迟初始化
    override fun getFunctions(): List<ExtFunction> {
        // 如果函数列表为空，进行初始化
        if (functions == null) {
            val extclass = Load.getExtclassByName(className)
            /*
            // 构建函数列表
            functions = extclass?.allMethods?.map { method ->
                ExtFunction(
                    name = method.name,
                    text = method.html,
                    parameters = method.params.map { param ->
                        FunctionParameter(
                            name = param.name,
                            type = null // 根据需求设定参数类型
                        )
                    }
                )
            } ?: listOf() // 如果 extclass 为 null，则返回空列表
             */
        }
        return functions ?: listOf()
    }
    //EXT原生对象不存在PSI，无需冲渲染
    override fun reParse(psi:PsiElement): Boolean {
        return false
    }
    // 用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        println("getCode extbase "+className)
        // 获取上下文对象
        // 返回空列表作为占位符

        val extclass = Load.getExtclassByName(className) ?: return listOf()
        //这种是输入.的情况
        /*
        if(psiElement.text=="IntellijIdeaRulezzz"){

        }

         */
        val lookupElements=extclass.getAllLookupElements("Method","Property","Event")
        println("getCode extbase ok"+lookupElements.size)
        return lookupElements
    }
    // 选择性地实现处理类的附加逻辑
    override fun toString(): String {
        return "ExtBaseClass(className='$className', functions=$functions)"
    }

}