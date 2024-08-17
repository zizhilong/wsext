package com.daima.exthelp.ext.extclass.extview

import com.daima.exthelp.Exp.PExp.Parser
import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.extclass.extfunc.ExtFunc
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Extclass
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

class ExtViewListeners (
    val jsObject: JSObjectLiteralExpression,
    val className:String,
): CodeHelpInterface
{
    init{
        jsObject.putUserData(CODE_HELP_KEY, this)
    }
    override fun reParse(psi: PsiElement):Boolean{
        //如果已经渲染过可以直接返回
        //TODO 处理渲染逻辑，需要拉取对应的class，主要的目的是封装内部函数调用处理一个作用域
        return true
    }
    init {
        // 初始化时尝试创建下级对象
        initializeSubItems()
    }
    // 初始化子项
    private fun initializeSubItems() {
        //处理子对象
        // 遍历所有属性
        jsObject.properties.forEach { property ->
            val value = property.value
            // 如果属性值是一个函数PSI
            if (value is JSFunction) {
                // 创建ExtFunc对象并传递PSI和一个返回Context的函数
                if(property.name==null){
                    return
                }
                ExtFunc(value,fun(): Context {
                    var ctx=Context()
                    var thisCls =Pool.findByClassName<Any>(jsObject.project,className);
                    if(thisCls==null){
                        return ctx
                    }
                    ctx.addClass("this",thisCls)

                    //获得事件参数
                    var extclass=Load.getExtclassByName(className)
                    val eventSuggestions = extclass?.getEventData(property.name!!)//getLookupElementsByPrefix(getPrefix(psiElement), "Event")
                    eventSuggestions?.params?.forEach { p ->
                        if(p.name=="self"){
                            ctx.addOptionalClass(thisCls)
                        }
                        p.valueType
                    }
                    return ctx
                })
            }
        }
    }
    //获得一个函数的上下文处理函数
    //首先这个对象表达的是是一个JSOBJ类型的事件绑定表，一般用于View的item中的listener属性，也用于控制器中的事件绑定函数
    //这个方法的目的，是通过查询特定一个被调用的函数名称，寻找这个函数的上下文定义。因为这个对象知道这个被调用函数是由那个对象的那个事件进行触发
    //能够清楚相关的事件类型
    fun getFunByName(name:String):Context?{
        jsObject.properties.forEach { property ->
            if(property.name==null){
                return null
            }

            val value = property.value
            if(value is JSReferenceExpression){
                //如果命中了特定函数
                if(name==getFullReferenceName(value)){
                    var ctx=Context()
                    var thisCls =Pool.findByClassName<Any>(jsObject.project,className);
                    if(thisCls==null){
                        return ctx
                    }
                    ctx.addClass("this",thisCls)
                    //获得事件参数
                    var extclass=Load.getExtclassByName(className)
                    val eventSuggestions = extclass?.getEventData(property.name!!)//getLookupElementsByPrefix(getPrefix(psiElement), "Event")
                    eventSuggestions?.params?.forEach { p ->
                        if(p.name=="self"){
                            ctx.addOptionalClass(thisCls)
                        }else{
                            var vtype=p.valueType
                            if(vtype==null){
                                ctx.addOptionalClass(null)
                            }else{
                                // 检查 valueType 是否以 "Ext." 开头
                                if (vtype!!.startsWith("Ext.")) {
                                    // 查找类并插入 ctx
                                    val extClass = Pool.findByClassName<Any>(jsObject.project,vtype)
                                    ctx.addOptionalClass(extClass)
                                } else {
                                    // 处理其他情况
                                    ctx.addOptionalClass(null)
                                }
                            }

                        }
                    }
                    return ctx
                }
            }
        }
        return null
    }
    // 假设 getCodeSuggestions 是用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        val obj = Parser("LREO").RunExp(psiElement)

        // 如果属于属性设定并且对象与jsObject相同
        if (obj != null && obj.lastPsi === jsObject) {
            val extclass = Load.getExtclassByName(className) ?: return listOf()

            // 获取事件类型的代码提示项
            val eventSuggestions = extclass.getLookupElementsByPrefix(getPrefix(psiElement), "Event")

            // 遍历事件建议列表，生成代码模板
            return eventSuggestions.map { element ->
                val eventData = element.getUserData(Extclass.EVENT_DATA)

                if (eventData != null) {
                    // 确保params不为null
                    val params = eventData.params?.joinToString(", ") { param ->
                        "${param.name}: ${param.valueType}"
                    } ?: ""

                    // 创建新的LookupElementBuilder，附加自定义的InsertHandler
                    element.withInsertHandler(eventInsertHandler) // 附加自定义的InsertHandler
                } else {
                    element // 如果没有事件数据，直接返回原始元素
                }
            }
        }
        return listOf()
    }
    fun getFullReferenceName(referenceExpression: JSReferenceExpression): String? {
        val references = mutableListOf<String>()

        // 从给定的 JSReferenceExpression 开始收集引用名称
        collectReferenceNames(referenceExpression, references)

        // 将所有收集到的引用名称拼接成一个完整的字符串
        return if (references.isNotEmpty()) {
            references.joinToString(".")
        } else {
            null
        }
    }

    private fun collectReferenceNames(referenceExpression: JSReferenceExpression, references: MutableList<String>) {
        // 递归地获取所有前缀的引用名称
        val qualifier = referenceExpression.qualifier
        if (qualifier is JSReferenceExpression) {
            collectReferenceNames(qualifier, references)
        }

        // 获取当前引用表达式的名称
        val referenceName = referenceExpression.referenceName
        if (referenceName != null) {
            references.add(referenceName)
        }
    }

// 自定义InsertHandler，用于在选择代码提示后插入代码模板
val eventInsertHandler = InsertHandler<LookupElement> { context, item ->
    val editor = context.editor
    val document = editor.document
    val eventData = item.getUserData(Extclass.EVENT_DATA)

    if (eventData != null) {
        // 确保params不为null
        val params = eventData.params?.joinToString(", ") { param ->
            "${param.name} /*${param.valueType}*/"
        } ?: ""

        // 创建匿名函数
        val functionTemplate = ": function($params)\n {\n\t// TODO: Implement event handler\n}"
        val insertPosition = context.tailOffset
        document.insertString(insertPosition, functionTemplate)

        // 移动光标到函数体内
        editor.caretModel.moveToOffset(insertPosition + functionTemplate.indexOf('{') + 1)
    }
}
}