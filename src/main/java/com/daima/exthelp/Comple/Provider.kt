package com.daima.exthelp.Comple
import com.daima.exthelp.Comple.Runner.ClassParam
import com.daima.exthelp.Comple.Runner.CompleRunner
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class Provider : CompletionProvider<CompletionParameters>() {
    // To add Xtype, uncomment the next line and add Xtype() to the list
    // runners = listOf(ClassParam(), Xtype())

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {

    }
}