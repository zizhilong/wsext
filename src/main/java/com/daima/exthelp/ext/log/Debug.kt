package com.daima.exthelp.ext.log

fun Info(str:String){
    // 创建一个 Throwable 对象来获取堆栈跟踪
    val stackTrace = Throwable().stackTrace
    // 检查堆栈跟踪的第二个元素（第一个是当前方法）
    val callerClassName = if (stackTrace.size > 1) {
        stackTrace[1].className
    } else {
        "Unknown"
    }

    println("ExtLog: $callerClassName: $str")
}