package com.daima.exthelp.ext.extclass

import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.extdata.Load

// 定义一个用于描述 Ext. 开头基类信息的类
class ExtBaseClass(
    private val className: String
) : ClassInterface<Any> {

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

    // 选择性地实现处理类的附加逻辑
    override fun toString(): String {
        return "ExtBaseClass(className='$className', functions=$functions)"
    }
}