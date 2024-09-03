package com.daima.exthelp.ext.comple.mod

import com.daima.exthelp.Tools.Dbdata
import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement

class Dbm : modbase() {

    // 手动指定的二级类目和名称的 Map
    private val TableClassMap: MutableMap<String, String> = mutableMapOf(
        "mzd" to "门诊",
        "mzs" to "门诊",
        "jcd" to "检查",
        "jcs" to "检查",
        "sfs" to "收费项目",
        "xt" to "系统",
        "yfd" to "药房",
        "yps" to "药品",
        "zyd" to "住院",
        "zys" to "住院",
    )
    private val FuncMap: MutableMap<String, String> = mutableMapOf(
        "newStore" to "创建空Store",
        "newTreeStore" to "创建空TreeStore，需要传ParentId",
    )
    private val remarkMap: MutableMap<String, String> = mutableMapOf(
        "JCS_BWDYFF" to "检查部位对应方法表",
        "JCS_JCBW" to "检查部位表",
        // ... (其他表注释)
    )

    init {
        // 初始化时加载数据库数据
        Dbdata.loadData()
    }

    fun Run(psi: PsiElement): List<LookupElement> {
        val continuousText = getContinuousText(psi)

        // 判断是否是以 "Dbm" 开头
        if (!continuousText.startsWith("Dbm")) {
            return listOf()
        }

        // 将 continuousText 变成数组
        val parts = continuousText.split('.').map { it.trim() }

        // 判断数组长度为1时，执行 tableclass
        return when (parts.size) {
            1 -> tableclass()
            2 -> {
                val lastPart = parts.last().lowercase()
                filterAndGenerateLookupElements(lastPart)
            }
            3 -> funclist()
            else -> listOf()
        }
    }

    private fun filterAndGenerateLookupElements(prefix: String): List<LookupElement> {
        //TODO 还需要实现
        /*
        val lookupList = mutableListOf<LookupElement>()
        for ((key, v) in Dbdata.tableComments) {
            val lowerKey = key.lowercase()
            if (lowerKey.startsWith(prefix)) {
                val formattedName = key.split('_').getOrNull(1)?.lowercase()?.replaceFirstChar { it.uppercase() } ?: continue

                val lookupElement = LookupElementBuilder.create(formattedName)
                    .withTypeText(v)
                    .withIcon(AllIcons.Nodes.DataTables)
                lookupList.add(PrioritizedLookupElement.withPriority(lookupElement, 1000000000.0))
            }
        }

         */

        //return lookupList
        return listOf()
    }

    private fun tableclass(): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()
        for ((key, value) in TableClassMap) {
            val lookupElement = LookupElementBuilder.create(key)
                .withTypeText(value)
                .withIcon(AllIcons.Gutter.DataSchema)
                .withInsertHandler(DotInsertHandler)
            lookupList.add(PrioritizedLookupElement.withPriority(lookupElement, 1000000000.0))
        }
        return lookupList
    }

    private fun funclist(): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()
        for ((key, value) in FuncMap) {
            val lookupElement = LookupElementBuilder.create(key)
                .withTypeText(value)
                .withIcon(AllIcons.Nodes.Function)
                .withInsertHandler(DotInsertHandler)
            lookupList.add(PrioritizedLookupElement.withPriority(lookupElement, 1000000000.0))
        }
        return lookupList
    }

    private object DotInsertHandler : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val document = context.document
            val editor = context.editor

            document.insertString(context.selectionEndOffset, ".")
            context.commitDocument()

            editor.caretModel.moveToOffset(context.selectionEndOffset)

            AutoPopupController.getInstance(context.project).autoPopupMemberLookup(editor, null)
        }
    }
}