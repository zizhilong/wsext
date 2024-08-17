package com.daima.exthelp.ext.extclass.extfunc

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.ext.log.Info
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSThisExpression
import com.intellij.psi.PsiElement
var debugid=false
fun getExpressionClass(jscontext: Context, element:PsiElement):CodeHelpInterface?{
    // 遍历JSExpressionStatement的子元素
        if (element is JSReferenceExpression) {
            // 获取引用名称
            val cls=getRefClassInterface(jscontext,element)
            if(cls!==null) {
                element.putUserData(CODE_HELP_KEY, cls as CodeHelpInterface)
                return cls
            }
        }
        //函数调用表达 this.control(...)
        if (element is JSCallExpression) {
            //引用性函数表达,并且使用了一个引用表达式
            if(element.children.size==2 &&element.children[0] is JSReferenceExpression){
                debugid=true
                val cls=getRefClassInterface(jscontext,element.children[0] as JSReferenceExpression)
                if(cls!==null) {
                    element.putUserData(CODE_HELP_KEY, cls as CodeHelpInterface)
                    return cls
                }
            }
        }
        if(element is JSThisExpression){
            return jscontext.getClass("this") as CodeHelpInterface?
        }
    return null
}

fun getRefClassInterface(context: Context,psi :JSReferenceExpression):CodeHelpInterface?{
    Info("getRefClassInterface")

    //如果是a.如果只有一个声明单元，说明在最里层
    if(psi.children.size==1 ){
        var chi=psi.children[0]
        //如果表示This
        return context.getClass(chi.text) as CodeHelpInterface?
    }
    //this.xxx 第一项代表this，后边代表this的属性
    if (psi.children.size==3 && psi.children[0] is JSThisExpression){
        //context.getClass("this")
        var cls=context.getClass("this")

        //处理this.xxx这种，如果xxx含有IntellijIdeaRulezzz，则表示处理this的属性
        if(cls !=null){
            if(psi.children[2].text.contains("IntellijIdeaRulezzz")){
                return cls as CodeHelpInterface
            }else{
                if(debugid){
                    var a=1
                }
                 return cls.getChildren(context, psi.children[2].text)
            }

        }
    }

    if (psi.children.size==3 && psi.children[0] is JSReferenceExpression){
        var chi=psi.children[0]
        //如果是向下赋值
        if(chi is JSReferenceExpression){
            var parentCls=getRefClassInterface(context,chi)
            if(parentCls!=null){
                chi.putUserData(CODE_HELP_KEY,parentCls as CodeHelpInterface)
                if(parentCls is ClassInterface<*>){
                    return parentCls.getChildren(context,psi.children[2].text)
                }
            }
        }
    }
    return null
}