package com.example.demo2.ui.toolsbar


import com.example.demo2.Exp.SExp.Parser
import com.example.demo2.Tools.StringHelper.trimQuotes
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode

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

            //获得
            val title= obj.findProperty("title")?.value?.text ?: ""
            val enode=ExtNode(extend,title)
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
                val xtype=item.findProperty("xtype")?.value?.text ?: ""
                var text=item.findProperty("text")?.value?.text ?: ""
                if(text==""){
                     text=item.findProperty("boxLable")?.value?.text ?: ""
                }
                text=trimQuotes(text)
                val enode=ExtNode(xtype,text)
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
}

