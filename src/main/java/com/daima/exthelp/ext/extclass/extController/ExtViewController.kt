package com.daima.exthelp.ext.extclass.extController

import com.daima.exthelp.ext.extclass.Context
import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.ext.extclass.ExtClass
import com.daima.exthelp.ext.extclass.ExtFunction
import com.daima.exthelp.ext.extclass.extview.ExtViewClass
import com.daima.exthelp.ext.extclass.extview.ExtViewItemClass
import com.daima.exthelp.ext.interfaces.ClassInterface
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.ext.log.Info
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement

// 继承 ExtClass
class ExtViewController(
    private val className: String,
    jsObject: JSObjectLiteralExpression,
    parentClass: ClassInterface<Any>? = null, // 可选的父类
    functions: List<ExtFunction> = listOf()   // 函数列表
) : ExtClass(className, jsObject, parentClass, functions) {
    public var extViewItem: ExtViewItemClass? = null
    //渲染当前view
    override fun renderPage() {
        super.renderPage()
        Info("renderPage")
        //处理init函数
        ExtVcInit(jsObject,this.getContext())

        //extViewItem = ExtViewItemClass(jsObject)

    }
    override fun getChildren(ctx:Context, name:String): CodeHelpInterface? {
        Info("getChildren")
        //如果是是调用
        //返回的ClassInterface应该代表了返回值类型的后续处理能力
        if(name=="control"){
            //这个函数是没返回值的
            //className
            return ExtVcInitControl(ctx,removeVcSuffix(className)) as CodeHelpInterface
        }
        //需要返回一个CodeHepler
        if(name=="getReferences"){
            return ExtViewReferences(this) as CodeHelpInterface
            //取得View对象
        }
        return super.getChildren(ctx,name)
    }
    fun getView():ExtViewClass?{
        val view = Pool.findByClassName<Any>(jsObject.project, removeVcSuffix(className))
        if (view !is ExtViewClass) {
            return null
        }
        return view
    }

    //取得特定函数的上下文
    override fun getContext(funName: String): Context {
        var ctx=super.getContext(funName)
        //如果取的上下文时找到了一个函数，则需要尝试找到函数的调用者传参，这种一般是在ExtClass去调用
        if(funName!=""){
            //寻找到Control处理对象函数
            var exp=Parser("Y{name init}FBS{name control}C")
            exp.debug=true
            var call =exp.Run(this.jsObject)
            if(call==null){
                return ctx
            }
            //jsObject
            //需要自行寻找Psi对象传入
            var ctl=ExtVcInitControl(ctx,removeVcSuffix(className))
                ctl.reParse(call)
                ctl.getExtViewListenersList().forEach{
                    l->

                    var fctx=l.getFunByName(funName)
                    //找到了这个函数专用的执行域
                    if(fctx!=null){
                        fctx.addClass("this",this)
                        return fctx
                    }
                }
        }
        return ctx
    }

    // 添加特定于 ExtViewClass 的属性或方法
    fun GetjsObject(): JSObjectLiteralExpression {
        return jsObject
    }
    fun removeVcSuffix(input: String): String {
        // 使用正则表达式匹配以“Vc”结尾的字符串
        val regex = "VC$".toRegex()
        // 使用 replace 方法将匹配的部分替换为空字符串
        return regex.replace(input, "")
    }
}