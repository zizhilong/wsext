package com.daima.exthelp.extdata

import com.daima.exthelp.Tools.StringHelper.trimQuotes
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

object Load {
    private val extclassList: List<Extclass> = loadJsonData()
    val xtypeMap: Map<String, String> = createXtypeMap(extclassList)
    private val nameToExtclassMap: Map<String, Extclass> = createNameToExtclassMap(extclassList)

    init {
        // 定义EXT对象分级类名map
        val extClassMap = buildTreeMap(extclassList)
    }

    private fun loadJsonData(): List<Extclass> {
        val objectMapper = ObjectMapper()
        return try {
            val inputStream = Load::class.java.classLoader.getResourceAsStream("data.json")
            objectMapper.readValue(inputStream, object : TypeReference<List<Extclass>>() {})
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun createXtypeMap(extclassList: List<Extclass>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (extclass in extclassList) {
            extclass.xtype?.forEach { xtype ->
                map[xtype] = extclass.name ?: ""
            }
        }
        return map
    }

    private fun createNameToExtclassMap(extclassList: List<Extclass>): Map<String, Extclass> {
        val map = mutableMapOf<String, Extclass>()
        for (extclass in extclassList) {
            extclass.name?.let { map[it] = extclass }
        }
        return map
    }

    fun getExtclassByName(name: String): Extclass? {
        val trimmedName = trimQuotes(name)
        val actualName = xtypeMap[trimmedName] ?: trimmedName
        return if (actualName.startsWith("Ext.")) {
            nameToExtclassMap[actualName]
        } else {
            null
        }
    }

    fun buildTreeMap(extclassList: List<Extclass>): Map<String, Any?> {
        val treeMap = mutableMapOf<String, Any?>()

        for (extclass in extclassList) {
            val parts = extclass.name?.split("\\.") ?: continue
            var currentMap = treeMap

            for (i in parts.indices) {
                val part = parts[i]
                if (i == parts.lastIndex) {
                    // 最后一部分，设置为叶子节点
                    currentMap[part] = null
                } else {
                    // 中间部分，设置为新的Map
                    currentMap = currentMap.computeIfAbsent(part) { mutableMapOf<String, Any?>() } as MutableMap<String, Any?>
                }
            }
        }

        return treeMap
    }
}