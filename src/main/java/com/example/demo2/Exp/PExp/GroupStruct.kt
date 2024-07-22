package com.example.demo2.Exp.PExp

import com.example.demo2.Tools.ExpHelper
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import java.util.regex.Pattern

class GroupStruct(argSe: IStringExpression) {
    private val LOG: Logger = Logger.getInstance(GroupStruct::class.java)

    var isOut: Boolean = false
    var min: Int = 1
    var max: Int = 1
    var se: IStringExpression
    var next: GroupStruct? = null
    private val ess: MutableList<Struct> = ArrayList()

    init {
        se = argSe
        var isOr = true
        while (se.getFirst().isNotEmpty()) {
            // 如果是对象开头
            if (ExpHelper.SPECIFIC_CHARACTERS.contains(se.getFirst())) {
                if (isOr) {
                    val newEs = Struct(se)
                    ess.add(newEs)
                    se = newEs.se
                    isOr = false
                    continue
                } else {
                    break;
                }
            }
            // 如果是或者
            if (se.getFirst() == "|") {
                if (ess.isEmpty()) {
                    throw ArithmeticException("或者条件下没有前置定义")
                }
                se.removeLeftCharacters(1)
                isOr = true
                continue
            }
            // 如果是数字
            if (NUMBER_CHARACTERS.contains(se.getFirst())) {
                var numStr = ""
                while (NUMBER_CHARACTERS.contains(se.getFirst()) && se.getFirst().isNotEmpty()) {
                    numStr += se.getFirst()
                    se.removeLeftCharacters(1)
                }
                val number = numStr.toInt()
                min = number
                max = number
                continue
            }
            // 如果是数值范围
            if (se.getFirst() == "[") {
                val sizeStr = se.getSubstringFromEnd(']')
                if (sizeStr.isEmpty()) {
                    throw ArithmeticException("范围字符串为空")
                }
                val pattern = Pattern.compile("\\[(\\d+)-(\\d+)]")
                val matcher = pattern.matcher(sizeStr)
                if (matcher.matches()) {
                    min = matcher.group(1).toInt()
                    max = matcher.group(2).toInt()
                } else {
                    throw ArithmeticException("范围字符串格式不正确")
                }
                se.removeLeftCharacters(sizeStr.length)
                continue
            }
            if (se.getFirst() == "#") {
                isOut = true
                se.removeLeftCharacters(1)
                continue
            }
            throw ArithmeticException("关键字不正确" + se.getFirst())
        }

        if (ess.isEmpty()) {
            throw ArithmeticException("ExpNumStruct未解析出任何节点")
        }
    }

    fun run(psi: PsiElement?, isTest: Boolean): Result? {
        val ret = Result()
        if (psi == null) {
            return if (min == 0) {
                Result()
            } else {
                null
            }
        }
        var num = 0
        var currentPsi: PsiElement? = psi
        while (true) {
            if (!isTest && num >= min && next != null) {
                if (next!!.run(currentPsi, true) != null) {
                    ret.nextPsi = currentPsi
                    return ret
                }
            }
            var findPsi = false
            for (ens in ess) {
                if (ens.runExp(currentPsi)) findPsi = true
            }
            if (!findPsi) {
                break
            } else if (isOut) {
                // ret.result.add(psi)
            }
            ret.lastPsi = currentPsi

            currentPsi = currentPsi!!.parent
            LOG.info("NetParent " + currentPsi!!.javaClass.typeName)
            if (currentPsi == null) {
                break
            }
            num++
            if (num == max) {
                break
            }
        }
        if (num < min || num > max) {
            return null
        }
        if (currentPsi != null) {
            LOG.info("ExpNumStructReturn" + currentPsi.javaClass.typeName)
        }
        ret.nextPsi = currentPsi
        return ret
    }

    companion object {
        private const val NUMBER_CHARACTERS = "0123456789"
    }
}