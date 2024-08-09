package com.daima.exthelp.jsdebug

import com.intellij.execution.filters.FilterMixin
import org.apache.lucene.document.Document
import java.util.function.Consumer

class guolv: FilterMixin {
    override fun shouldRunHeavy(): Boolean {
        // 返回是否应运行重过滤
        return false // 假设我们不需要重过滤
    }

    override fun applyHeavyFilter(
        copiedFragment: com.intellij.openapi.editor.Document,
        startOffset: Int,
        startLineNumber: Int,
        consumer: com.intellij.util.Consumer<in FilterMixin.AdditionalHighlight>
    ) {

        // 重过滤逻辑
        /*
        val text = copiedFragment.text
        val index = text.indexOf("error") // 示例：过滤包含 "error" 的行
        if (index >= 0) {
            consumer.consume(FilterMixin.AdditionalHighlight(startOffset + index, startOffset + index + "error".length))
        }

         */
    }

    override fun getUpdateMessage(): String {
        // 返回更新消息
        return "WebConsole filtering updated"
    }
}