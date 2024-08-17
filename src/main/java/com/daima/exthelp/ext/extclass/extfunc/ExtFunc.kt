package com.daima.exthelp.ext.extclass.extfunc

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.ext.log.Info
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.psi.JSBlockStatement
import com.intellij.lang.javascript.psi.JSExpressionStatement
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

// ExtFunc类，实现CodeHelpInterface接口
class ExtFunc(
    private val jsFunc: JSFunction,
    private val contextProvider: () -> Context
) : CodeHelpInterface {
    init {
        Info("init")
        // 在JSFunction上设置用户数据
        jsFunc.putUserData(CODE_HELP_KEY, this)
    }

    override fun reParse(psi:PsiElement): Boolean {
        Info("reParse")
        //已经渲染过不在渲染
        //获得上下文。开始重新渲染
        val context = contextProvider()
        // 比对JS函数参数与上下文可选类列表
        compareParametersWithOptionalClasses(context)
        accessJSExpressionsInFunctionBody(context)
        // 返回true表示运行中
        return true
    }

    // 用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        Info("getCodeSuggestions")
        // 获取上下文对象
        // 返回空列表作为占位符
        return listOf()
    }
    /**
     * 访问JSFunction的JSBlockStatement下的所有JSExpression对象。
     */
    private fun accessJSExpressionsInFunctionBody(context:Context) {
        // 获取JSFunction的主体块
        val body = jsFunc.block
        // 检查主体是否为JSBlockStatement
        if (body is JSBlockStatement) {
            //处理函数主体内容
            body.children.forEach { statement ->
                //处理函数体的直接调用 比如 gird.
                if (statement is JSExpressionStatement) {
                    ExtExpressionStatemen(statement,context)
                }
                //函数体中的赋值处理
                if(statement is JSVarStatement){
                    ExtVar(statement,context)
                }
            }
        }
    }

    /**
     * 比较JS函数的参数和上下文中的可选类列表，并根据匹配添加到上下文中。
     * @param context 当前的上下文对象
     */
    private fun compareParametersWithOptionalClasses(context: Context) {
        // 获取JS函数的参数
        val jsParameters = jsFunc.parameters

        // 获取可选类列表
        val optionalClasses = context.getOptionalClassList()

        // 获取参数数量和可选类数量的最小值
        val minCount = minOf(jsParameters.size, optionalClasses.size)

        // 比对参数和可选类列表
        for (i in 0 until minCount) {
            val parameter = jsParameters[i]
            val optionalClass = optionalClasses[i]

            // 如果可选类不为null，则将参数名称和类添加到上下文中
            if (optionalClass != null && parameter is JSParameter) {
                context.addClass(parameter.name ?: "unknown", optionalClass)
            }
        }
    }
}