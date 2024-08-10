package com.daima.exthelp.extdata

import com.daima.exthelp.InsertHandler.newEvent.InsertEvent
import com.fasterxml.jackson.annotation.JsonProperty
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.Key
import com.intellij.util.ProcessingContext

/**
 * Extclass类表示一个具有多个属性和方法的扩展类。
 * 它支持从JSON反序列化，并提供代码提示项的生成。
 */
class Extclass {

    @JsonProperty("Name")
    val name: String? = null // 类名

    @JsonProperty("Xtype")
    val xtype: List<String>? = null // 类的xtype列表

    @JsonProperty("Config")
    val config: List<Properties>? = null // 配置属性列表

    @JsonProperty("Properties")
    val properties: List<Properties>? = null // 普通属性列表

    @JsonProperty("Methods")
    val methods: List<Methods>? = null // 方法列表

    @JsonProperty("Events")
    val events: List<Events>? = null // 事件列表

    @JsonProperty("Html")
    val html: String? = null // 类的HTML描述

    @JsonProperty("Extends")
    val extendsClass: String? = null // 父类名

    @JsonProperty("Mixins")
    val mixins: List<String>? = null // 混入类列表

    // 存储根据前缀字母的所有代码提示项
    private val lookupElementsByPrefix: MutableMap<Char, Array<LookupElementBuilder>> = mutableMapOf()

    companion object {
        private val IS_EVENT = Key<String>("IS_EVENT") // 用于标识事件的键
        val EVENTDATA = Key<Events>("EVENTDATA") // 存储事件数据的键
        val TYPE = Key<String>("TYPE") // 存储类型信息的键
        val ORIGINAL_DATA = Key<Any>("ORIGINAL_DATA") // 存储原始数据的键

        /**
         * 获取所有混入类和父类的名称列表。
         * @param obj 当前Extclass对象
         * @return 包含所有混入类和父类名称的列表
         */
        fun getAllMixinsAndSuperClasses(obj: Extclass?): List<String> {
            return buildSet {
                obj?.name?.let { add(it) }
                collectMixinsAndSuperClasses(obj, this)
            }.toList()
        }

        /**
         * 收集所有混入类和父类的名称。
         * 递归遍历父类树。
         * @param obj 当前Extclass对象
         * @param allMixinsAndSuperClasses 存储所有混入类和父类的集合
         */
        private fun collectMixinsAndSuperClasses(obj: Extclass?, allMixinsAndSuperClasses: MutableSet<String>) {
            obj?.let {
                // 添加当前对象的名称
                it.name?.let { allMixinsAndSuperClasses.add(it) }

                // 添加当前对象的mixins
                it.mixins?.let { allMixinsAndSuperClasses.addAll(it) }

                // 递归处理父类
                val parentClassName = it.extendsClass
                if (!parentClassName.isNullOrEmpty()) {
                    val parentObj = Load.getExtclassByName(parentClassName)
                    collectMixinsAndSuperClasses(parentObj, allMixinsAndSuperClasses)
                }
            }
        }
    }

    /**
     * 获取当前类及其所有父类的代码提示项。
     * @return 包含所有相关类的代码提示项列表
     */
    fun getSuperElementBuilder(): List<LookupElementBuilder> {
        return getAllMixinsAndSuperClasses(this).flatMap { className ->
            Load.getExtclassByName(className)?.getLookupElementBuilder().orEmpty()
        }
    }

    /**
     * 获取当前类的代码提示项。
     * @return 代码提示项列表
     */
    fun getLookupElementBuilder(): List<LookupElementBuilder> {
        val lookupElements = mutableListOf<LookupElementBuilder>()

        // 处理配置属性
        config?.forEach { cfg ->
            if (cfg.visibility == 0) { // 仅处理可见性为0的配置属性
                val builder = LookupElementBuilder.create(cfg.name)
                    .withTypeText("Config Property") // 设置类型文本为"Config Property"
                    .withIcon(AllIcons.General.Gear) // 设置图标
                    .withTailText(" ${cfg.valueType.joinToString(", ")}", true) // 设置尾文本

                if (cfg.readOnly) {
                    builder.withTailText(" (readonly)", true) // 如果属性只读，添加只读标记
                }
                builder.putUserData(TYPE, "Config") // 添加类型信息
                builder.putUserData(ORIGINAL_DATA, cfg) // 添加原始数据
                lookupElements.add(builder)
            }
        }

        // 处理普通属性
        properties?.forEach { prop ->
            if (prop.visibility == 0) { // 仅处理可见性为0的普通属性
                val builder = LookupElementBuilder.create(prop.name)
                    .withTypeText("Property") // 设置类型文本为"Property"
                    .withIcon(AllIcons.Nodes.Property) // 设置图标
                    .withTailText(" ${prop.valueType.joinToString(", ")}", true) // 设置尾文本

                if (prop.readOnly) {
                    builder.withTailText(" (readonly)", true) // 如果属性只读，添加只读标记
                }
                builder.putUserData(TYPE, "Property") // 添加类型信息
                builder.putUserData(ORIGINAL_DATA, prop) // 添加原始数据
                lookupElements.add(builder)
            }
        }

        // 添加方法的代码提示项
        methods?.forEach { method ->
            if (method.visibility == 0) { // 仅处理可见性为0的方法
                val e = LookupElementBuilder.create(method.name)
                    .withTypeText("Method") // 设置类型文本为"Method"
                    .withIcon(AllIcons.Nodes.Method) // 设置方法图标
                    .apply {
                        putUserData(TYPE, "Method") // 添加类型信息
                        putUserData(ORIGINAL_DATA, method) // 添加原始数据
                    }
                lookupElements.add(e)
            }
        }

        // 添加事件的代码提示项
        events?.forEach { evt ->
            val e = LookupElementBuilder.create(evt.name)
                .withTypeText("Event") // 设置类型文本为"Event"
                .withIcon(AllIcons.Actions.Lightning) // 设置事件图标
                .apply {
                    putUserData(IS_EVENT, "") // 设置用户数据标识为事件
                    putUserData(EVENTDATA, evt) // 关联事件数据
                    putUserData(TYPE, "Event") // 添加类型信息
                    putUserData(ORIGINAL_DATA, evt) // 添加原始数据
                }
            lookupElements.add(e)
        }

        return lookupElements
    }

    /**
     * 根据类和父类生成所有代码提示项，并按前缀字母进行存储。
     */
    private fun generateLookupElements() {
        lookupElementsByPrefix.clear() // 清空现有的代码提示项

        // 获得全部上级的代码提示项
        val lookupElements = getSuperElementBuilder()

        // 根据前缀字母分组
        lookupElements.groupBy { it.lookupString.firstOrNull() ?: '#' }
            .forEach { (key, value) ->
                lookupElementsByPrefix[key] = value.toTypedArray() // 存储到前缀映射中
            }
    }

    /**
     * 获取指定前缀的代码提示项。
     * @param prefix 前缀字符
     * @param context 处理上下文
     * @return 代码提示项数组
     */
    /**
     * 获取指定前缀的代码提示项。
     * @param prefix 前缀字符
     * @param context 处理上下文
     * @param filterTypes 可变参数，指定要过滤的元素类型（如"Event"、"Method"、"Property"）
     * @return 代码提示项数组
     */
    fun getLookupElementsByPrefix(prefix: Char, vararg filterTypes: String): List<LookupElementBuilder> {
        if (lookupElementsByPrefix.isEmpty()) {
            generateLookupElements() // 如果提示项为空，生成提示项
        }
        val elements = lookupElementsByPrefix[prefix]?.toList() ?: return emptyList() // 返回对应前缀的提示项，或空列表

        // 如果指定了过滤器类型，过滤出符合类型的元素
        return if (filterTypes.isNotEmpty()) {
            elements.filter { it.getUserData(TYPE) in filterTypes }
        } else {
            elements
        }
    }
}