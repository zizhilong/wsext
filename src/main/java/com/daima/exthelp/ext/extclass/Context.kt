package com.daima.exthelp.ext.extclass

import com.daima.exthelp.ext.interfaces.ClassInterface

// 上下文类，包含一个 Map，以 String 为键，ClassInterface<T> 为值
class Context {

    // 使用 String 为键，ClassInterface<T> 为值的 Map
    private val classMap: MutableMap<String, ClassInterface<*>> = mutableMapOf()

    // 可选的 ClassInterface 列表
    private val optionalClassList: MutableList<ClassInterface<Any>?> = mutableListOf()

    // 添加一个类到上下文中
    fun addClass(name: String, classInterface: ClassInterface<*>) {
        classMap[name] = classInterface
    }

    // 根据名称获取类
    fun getClass(name: String): ClassInterface<*>? {
        return classMap[name]
    }

    // 获取上下文中的所有类名称
    fun getClassNames(): Set<String> {
        return classMap.keys
    }

    // 检查类是否存在于上下文中
    fun containsClass(name: String): Boolean {
        return classMap.containsKey(name)
    }

    // 添加一个 ClassInterface 到列表
    fun addOptionalClass(classInterface: ClassInterface<Any>?) {
        optionalClassList.add(classInterface)
    }

    // 获取可选的 ClassInterface 列表
    fun getOptionalClassList(): List<ClassInterface<Any>?> {
        return optionalClassList
    }

    // 输出上下文的内容
    override fun toString(): String {
        return "classMap=$classMap, optionalClassList=$optionalClassList"
    }
}