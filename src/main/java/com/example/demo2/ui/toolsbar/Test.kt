package com.example.demo2.ui.toolsbar


import com.example.demo2.Exp.SExp.Parser
import com.example.demo2.Tools.StringHelper.trimQuotes
import com.example.demo2.ui.toolsbar.xtypeIcon.Companion.getXtype
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiFile
import com.intellij.ui.IconManager
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import java.awt.Color
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.*
class Test : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

    }
    fun GetNode(psiFile :PsiFile):DefaultMutableTreeNode?{

        //val psiFile = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE)
        if ( psiFile != null) {
            //val document = editor.document
            //val psiDocumentManager = com.intellij.psi.PsiDocumentManager.getInstance(project)
            //psiDocumentManager.commitDocument(document)
            if (psiFile == null || psiFile !is JSFile) {
                return null;
            }
            var exp = Parser("SCR{value define}R{value Ext}<<aO")
            //第一个节点
            val extobj=exp.Run(psiFile) as JSObjectLiteralExpression
            //没找到EXT创建节点
            var OneNode = GetOneNode(extobj)
            //取子节点
            var cnodes:ArrayList<DefaultMutableTreeNode>?=GetChildNode(extobj.findProperty("items"));
            if(cnodes!=null){
                for(cnode in cnodes){
                    if (OneNode != null) {
                        OneNode.add(cnode)
                    }
                }
            }
            return OneNode
            //addNodeToTree(OneNode)
        }
        return null
    }
        fun GetOneNode( obj : JSObjectLiteralExpression): DefaultMutableTreeNode?{
            if(obj==null){
                return null
            }
            //获得基类
            var extend=obj.findProperty("extend")?.value?.text
            if(extend==null ||extend==""){
                return null
            }

            extend=trimQuotes(extend)
            if(getXtype(extend)!=null){
                extend=getXtype(extend)
            }
            //获得
            val title= obj.findProperty("title")?.value?.text ?: ""

            //创建节点
            val enode= ExtNode()
            val icon=xtypeIcon.getIcon(extend!!)
            if (icon != null) {
                enode.setIcon(icon)
            }
            enode.psi=obj
            //类型标题
            enode.addText(PresentableNodeDescriptor.ColoredFragment(extend, SimpleTextAttributes.REGULAR_ATTRIBUTES))
            //副标题
            if(title!=""){
                enode.addText(PresentableNodeDescriptor.ColoredFragment(title,
                    SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.BLUE)))
            }

            return DefaultMutableTreeNode(enode)
        }
    fun GetChildNode(obj:JSProperty?):ArrayList<DefaultMutableTreeNode>?{
        val ret=ArrayList<DefaultMutableTreeNode>()
        if(obj==null){
            return null
        }
        //如果item不是一个数组
        if(obj.value !is JSArrayLiteralExpression){
            return null
        }
        for (item in (obj.value as JSArrayLiteralExpression).children) {
            if(item is JSObjectLiteralExpression){
                //获得
                var xtype=item.findProperty("xtype")?.value?.text ?: ""
                var text=item.findProperty("text")?.value?.text ?: ""
                if(text==""){
                     text=item.findProperty("boxLable")?.value?.text ?: ""
                }
                if(text==""){
                    text=item.findProperty("title")?.value?.text ?: ""
                }
                xtype=trimQuotes(xtype)
                text=trimQuotes(text)
                val enode=ExtNode()
                enode.addText(PresentableNodeDescriptor.ColoredFragment(xtype, SimpleTextAttributes.REGULAR_ATTRIBUTES))
                if(text!=""){
                    enode.addText(PresentableNodeDescriptor.ColoredFragment(text,
                        SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.BLUE)))
                }
                val icon=xtypeIcon.getIcon(xtype)
                 if (icon != null) {
                    enode.setIcon(icon)
                }
                enode.psi=item
                val node=DefaultMutableTreeNode(enode)
                    val cnodes=GetChildNode(item.findProperty("items"))
                    if(cnodes!=null){
                        for(cnode in cnodes){
                            node.add(cnode)
                        }
                    }
                ret.add(node)
            }
        }
        return ret;
    }
    fun capitalizeFirstLetter(text: String): String {
        return text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }


    //icons\toolbox\common

}

