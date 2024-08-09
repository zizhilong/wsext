package com.daima.exthelp.jsdebug

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.ShowTextPopupHyperlinkInfo
import com.intellij.javascript.debugger.console.legacy.JSConsoleView
import com.intellij.javascript.jest.snapshot.JestUpdateSnapshotHyperlinkInfo
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.Pair
import org.jetbrains.annotations.NonNls
import java.io.File
import java.nio.charset.Charset

class  Filter : Filter{

    private var jv: JSConsoleView
    @Volatile @NlsSafe
    private var fileText: String? = null
    private var triggered = false

    constructor(@NlsSafe js: JSConsoleView) {
        jv=js
    }



    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        if (!triggered) {

        }

        return null
    }
}
