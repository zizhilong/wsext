package com.daima.exthelp.Inspection.model

import com.daima.exthelp.Exp.SExp.Parser
import com.daima.exthelp.Tools.JSObjectLiteralUnit
import com.daima.exthelp.Tools.StringHelper.trimQuotes
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.rd.generator.nova.PredefinedType

class ControllerModel(file :JSFile) {
    var file :JSFile?=file
    //返回绑定的所有事件控件表
    fun EventBindGroup(): List<ControllerEventBindGroup>{
        val exp = Parser("SCR{value define}R{value Ext}<<aOY{name init}FBSCaO")
        val o = exp.Run(this.file) as? JSObjectLiteralExpression ?: return emptyList()

        // 创建一个长度为 o.children.size 的 List
        val ret = mutableListOf<ControllerEventBindGroup>()

        for (p in o.children.filterIsInstance<JSProperty>()) {
            ret.add(ControllerEventBindGroup(p))
        }

        return ret
    }

    fun GetFunctionByName(funName:String):PsiElement?{
        val exp = Parser("SCR{value define}R{value Ext}<<aO")
        val obj = exp.Run(file) as JSObjectLiteralExpression ?: return null
        //创建帮助类
        val uobj=JSObjectLiteralUnit(obj)
        val p=uobj.findProperty(funName)?: return null
        if(p.value is JSFunction){
            return p.value
        }
        return null
    }
}
//事件绑定组
class ControllerEventBindGroup(p:JSProperty){
    //绑定的控件名
    val name: String = p.name?.let { trimQuotes(it) } ?: ""
    val value: PsiElement? = p.value
    fun EventBind():List<ControllerEventBind>{
        val o = value as? JSObjectLiteralExpression ?: return emptyList()
        // 创建一个长度为 o.children.size 的 List
        val ret = mutableListOf<ControllerEventBind>()

        for (p in o.children.filterIsInstance<JSProperty>()) {
            ret.add(ControllerEventBind(p))
        }
        return ret
    }
}
//具体绑定项
class ControllerEventBind(p:JSProperty){
    var name:String=""
    var funName:String=""
    var psi :PsiElement?=null
    init {
        psi=p
         name = p.name?.let { trimQuotes(it) } ?: ""
         if(p.value is JSReferenceExpression){
             val re=p.value
             if(re!!.children.size==3 && re!!.children[2] is LeafPsiElement){
                 funName=re!!.children[2].text
             }
         }
         //funName
    }

}