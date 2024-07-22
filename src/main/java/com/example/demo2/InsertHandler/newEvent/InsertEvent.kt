package com.example.demo2.InsertHandler.newEvent

import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.example.demo2.Exp.PExp.Parser
import com.example.demo2.Tools.JSArrayLiteralUnit
import com.example.demo2.Tools.JSObjectLiteralUnit
import com.example.demo2.Tools.comment
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VfsUtil
import org.jetbrains.annotations.NotNull
import java.io.IOException
import java.util.regex.Pattern

class InsertEvent {
    class InlineInsertHandler : InsertHandler<LookupElement> {
        private val LOG = Logger.getInstance(InlineInsertHandler::class.java)

        override fun handleInsert(@NotNull context: InsertionContext, @NotNull item: LookupElement) {
            val psiFile = context.file
            val offset = context.startOffset
            val elementAtCaret = psiFile.findElementAt(offset)
            //删除原有输入信息并插入一条注释信息




            val parent = elementAtCaret?.parent ?: return




            val eventObj = Parser("REO").RunExp(parent)?.lastPsi as? JSObjectLiteralExpression ?: return
            val itemidstr = eventObj.findProperty("itemId")?.value?.text

            val define = Parser(".[1-99]O{haveattr extend}").apply { debug = true }
            LOG.info("START")
            val expRet = define.RunExp(parent)
            LOG.info("END")
            if (expRet == null) {
                LOG.info("Event Null")
                return
            }

            //输入记录前加入注释
            parent.addBefore(comment(parent.project,"//Event:"+item.lookupString+""),elementAtCaret)
            elementAtCaret.delete()

            val extDefineObjPsi = expRet.lastPsi as? JSObjectLiteralExpression ?: return
            val project = context.project
            val projectBaseDir = project.baseDir ?: run {
                LOG.warn("projectBaseDir is null")
                return
            }
            val virtualFile = psiFile.virtualFile ?: run {
                LOG.warn("virtualFile is null")
                return
            }

            val fPath = virtualFile.path
            if (!fPath.startsWith(projectBaseDir.path)) {
                LOG.warn("file not in project")
                return
            }

            val vpathb = fPath.substring(projectBaseDir.path.length + 1)
            val appJsFile = projectBaseDir.findFileByRelativePath("app.js") ?: run {
                LOG.warn("appJsFile not found")
                return
            }

            val extProjectName = try {
                VfsUtil.loadText(appJsFile).let { extractNameValue(it) } ?: run {
                    LOG.warn("ExtProjectName not found")
                    return
                }
            } catch (e: IOException) {
                LOG.warn("ExtProjectName IOException")
                return
            }

            Log.info("ExtProjectName: $extProjectName")
            val extCls = vPathToExtClass(extProjectName, vpathb)
            Log.info("ExtCls: $extCls")

            var prop = extDefineObjPsi.findProperty("requires") ?: addRequiresProperty(extDefineObjPsi)
            val extClsForViewController = extCls+"ViewController"
            val requires = prop.value as? JSArrayLiteralExpression

            requires?.let {
                val arrUnit = JSArrayLiteralUnit(it)
                if (!arrUnit.findInString(extClsForViewController)) {
                    arrUnit.addString(extClsForViewController, null)
                }
                val vhelp=ViewControllerHelper(context.project, extClsForViewController)
                vhelp.createEventBind(itemidstr!!,item)
            }
        }

        private fun addRequiresProperty(extDefineObjPsi: JSObjectLiteralExpression): JSProperty {
            val f = JSObjectLiteralUnit(extDefineObjPsi)
            var prev: JSProperty? = null
            extDefineObjPsi.properties.forEach {
                if (it.name == "extend" || it.name == "alias") {
                    prev = it
                }
            }
            return f.addProperty("requires", "[]", prev)
        }
    }

    companion object {
        private fun vPathToExtClass(projectName: String, vPath: String): String {
            return "$projectName.${vPath.removePrefix("classic/src/").replace(".js", "").replace("/", ".")}"
        }

        private fun extractNameValue(fileContent: String): String? {
            val pattern = Pattern.compile("name\\s*:\\s*\"([^\"]*)\"")
            val matcher = pattern.matcher(fileContent)
            return if (matcher.find()) matcher.group(1) else null
        }
    }
}