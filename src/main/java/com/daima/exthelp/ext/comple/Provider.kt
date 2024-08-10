package com.daima.exthelp.ext.comple
import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.ExpHelper
import com.daima.exthelp.ext.extclass.extview.ExtViewClass
import com.daima.exthelp.ext.extclass.parseFile
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
class Provider : CompletionProvider<CompletionParameters>() {
    // To add Xtype, uncomment the next line and add Xtype() to the list
    // runners = listOf(ClassParam(), Xtype())

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {

        //获得当前指针节点
        val rawpsi: PsiElement = parameters.position
        //迭代PSI
        var text=(rawpsi as LeafPsiElement).text
        var element=rawpsi
        //获取当前页面
        //加载当前页面元素表
        val start = System.currentTimeMillis()
        var kaishicode=element.parent.parent.parent.hashCode()



        try {

        //渲染页面
            val psifile=findJSFile(element)
            if(psifile==null){
                return
            }
            /*
            //AOERL
            var itemProperty=Parser("SCaOY{name items}").Run(psifile)
            if(itemProperty is JSProperty) {
                // 检查 item 属性是否是数组
                if (itemProperty?.value is JSArrayLiteralExpression) {
                    val itemArray = itemProperty.value as JSArrayLiteralExpression
                    // 遍历数组中的每个元素
                    for (element in itemArray.expressions) {
                        if (element is JSObjectLiteralExpression) {
                            var xhid = element.hashCode()
                            xhid=xhid
                        }
                    }
                }
            }
            */

            val extclass= parseFile(psifile)
            if(extclass==null){
                return
            }
            extclass.renderPage()
            /*
            var items2=element.parent.parent.parent.parent.parent.parent
            var psicode =items2.hashCode()
                extclass.renderPage()



                if(extclass is ExtViewClass){
                    var items=extclass.GetjsObject()
                    var extViewItemObj=extclass.extViewItem!!.jsObject

                    var viewcode=items.hashCode()
                    psicode=psicode

                }
*/

            /*
            对比子对象
                if(extclass is ExtViewClass){
                    var items=extclass.extViewItem
                    items=items!!.getSubItems()[0]
                    items=items
                    var item3=items.jsObject
                    var items2=element.parent.parent.parent
                    var viewcode=item3.hashCode()
                    var psicode =items2.hashCode()
                    psicode=psicode
                }
            */
                val end = System.currentTimeMillis()
                val elapsedTime = end - start
                println("Elapsed time: $elapsedTime ms")
                //向前寻找
                 while(element!==null){
                    val codehelp=element.getUserData(CODE_HELP_KEY)
                    //如果存在Code帮助方法则退出
                     Logger.getInstance(Provider::class.java).warn("find CODE_HELP_KEY");
                    if(codehelp!=null){
                        result.addAllElements(codehelp.getCodeSuggestions(rawpsi,context))
                        break
                    }
                     //需要实际测试看
                    if(element.prevSibling==null){
                        if(element!=null){
                        element=element.parent
                        }
                    }else{
                        element=element.prevSibling
                    }
                }

        } catch (e: Exception) {
            println("Caught an ArithmeticException: ${e.message}")
        }
    }
    fun findJSFile(psiElement: PsiElement): JSFile? {
        var currentElement: PsiElement? = psiElement

        // 循环向上查找父节点
        while (currentElement != null) {
            // 检查当前节点是否是 JSFile
            if (currentElement is JSFile) {
                return currentElement
            }
            // 获取父节点
            currentElement = currentElement.parent
        }

        // 如果没有找到 JSFile，则返回 null
        return null
    }
}