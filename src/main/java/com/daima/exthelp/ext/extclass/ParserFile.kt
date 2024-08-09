package com.daima.exthelp.ext.extclass

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.StringHelper
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * 解析 JavaScript 文件并返回实现了 ClassInterface 的对象。
 * @param psiFile 要解析的 PSI 文件
 * @return 返回解析后的 ClassInterface 对象，若未找到则返回 null
 */
fun parseFile(psiFile: PsiFile): ClassInterface<*>? {
    // 初始化自定义解析器，以识别特定的模式
    val exp = Parser("SCR{value define}R{value Ext}<<a")

    // 使用解析器执行，并获取结果
    val obj: PsiElement? = exp.Run(psiFile) ?: return null

    // 检查解析结果是否为 JSArgumentList 实例
    if (obj is JSArgumentList) {
        // 从参数列表中获取第一个和第二个参数
        val firstArg = obj.arguments.getOrNull(0)
        val secondArg = obj.arguments.getOrNull(1)

        // 检查第一个参数是否为字符串字面量
        if (firstArg is JSLiteralExpression && firstArg.isStringLiteral) {
            // 提取字符串值，并去除引号
            val rawClassName = firstArg.value as? String ?: return null
            val className = StringHelper.trimQuotes(rawClassName)

            // 检查第二个参数是否为对象字面量
            if (secondArg is JSObjectLiteralExpression) {


                // 在对象字面量中查找名为 "extend" 的属性
                val extendProperty = secondArg.findProperty("extend")
                val extendClassName = (extendProperty?.value as? JSLiteralExpression)?.value as? String
                var upClass = extendClassName
                var baseClassName: String? = null // 用于存储找到的基类名
                if(extendClassName!=null){
                    var upClass=extendClassName
                    while (upClass!!.isNotEmpty()) {
                        // 尝试从池中查找该类
                        val parentClass = Pool.findByClassName<Any>(psiFile.project, upClass)

                        if (upClass.startsWith("Ext")) {
                            // 找到以 "Ext" 开头的类名，设置 baseClassName 并跳出循环
                            baseClassName = upClass
                            break
                        }

                        // 更新 upClass 为其父类名，假设父类名可以通过某种方式获取
                        upClass = parentClass?.getParent()?.getClassName() ?: ""
                    }
                }


                // 根据 baseClassName 创建不同的类实例
                val classInstance: ClassInterface<*> = when (baseClassName) {
                    "Ext.panel.Panel" -> {
                        // 创建 ExtPanel 类实例
                        ExtClass(className, secondArg, baseClassName?.let { Pool.findByClassName<Any>(psiFile.project, it) })
                    }
                    "Ext.window.Window" -> {
                        // 创建 ExtWindow 类实例
                        ExtClass(className, secondArg, baseClassName?.let { Pool.findByClassName<Any>(psiFile.project, it) })
                    }
                    // 添加其他基类的处理逻辑
                    else -> {
                        // 默认使用 ExtClass 创建实例
                        ExtClass(className, secondArg, baseClassName?.let { Pool.findByClassName<Any>(psiFile.project, it) })
                    }
                }
                // 返回创建的类实例
                return classInstance
            } else {
                // 如果第二个参数不是对象字面量，打印提示
                println("Second argument is not an object literal.")
            }
        }
    }
    // 如果没有找到符合条件的内容，返回 null
    return null
}