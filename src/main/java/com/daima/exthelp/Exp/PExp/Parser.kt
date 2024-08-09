package com.daima.exthelp.Exp.PExp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement

class Parser(expr: String) {
    private val LOG: Logger = Logger.getInstance(Parser::class.java)
    var debug = false
    private val enss: MutableList<GroupStruct> = ArrayList()

    init {
        //进行解析
        var se: IStringExpression = StringExpression(expr)
        while (true) {
            //如果字符串为空则直接退出
            if (se.getFirst().isEmpty()) {
                break;
            }
            val newns = GroupStruct(se)
            //如果没有则传下一个
            if (enss.isNotEmpty()) {
                enss[enss.size - 1].next = newns
            }
            enss.add(newns)
            se = newns.se
        }
    }

    fun RunExp(psi: PsiElement): Result? {
        val ret = Result()
        //交给数量解析器处理
        var thispsi :PsiElement
        thispsi=psi
        for (ens in enss) {
            // 处理每个 GroupStruct 对象
            val ensret = ens.run(thispsi, false) ?: run {
                LOG.info("GroupStruct 返回null")
                return null
            }
            //psi赋予上一个数量处理器的psi
            thispsi = ensret.nextPsi!!
            //把ENS返回的Psi节点传到上机返回值中
            ret.result.addAll(ensret.result)
            ret.nextPsi = psi
            ret.lastPsi = ensret.lastPsi
            if (debug) {
                LOG.info("GroupStructForNext")
            }
        }
        //LOG.info("Return:")
        return ret
    }
}