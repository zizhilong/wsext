package com.daima.exthelp.ext.interfaces

import com.intellij.openapi.util.Key

// 定义一个 Key 用于存储实现 CodeHelpInterface 的对象
val CODE_HELP_KEY: Key<CodeHelpInterface> = Key.create("codeHelp")