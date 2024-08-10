package com.daima.exthelp.extdata

import com.daima.exthelp.InsertHandler.newEvent.InsertEvent
import com.fasterxml.jackson.annotation.JsonProperty
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.Key
import com.intellij.util.ProcessingContext

class Extclass {

    @JsonProperty("Name")
    val name: String? = null

    @JsonProperty("Xtype")
    val xtype: List<String>? = null

    @JsonProperty("Config")
    val config: List<Properties>? = null

    @JsonProperty("Properties")
    val properties: List<Properties>? = null

    @JsonProperty("Methods")
    val methods: List<Methods>? = null

    @JsonProperty("Events")
    val events: List<Events>? = null

    @JsonProperty("Html")
    val html: String? = null

    @JsonProperty("Extends")
    val extendsClass: String? = null

    @JsonProperty("Mixins")
    val mixins: List<String>? = null

    // 存储根据前缀字母的所有代码提示项
    private var lookupElementsByPrefix: MutableMap<Char, Array<LookupElementBuilder>> = mutableMapOf()

    companion object {
        private val IS_EVENT = Key<String>("IS_EVENT")
        val EVENTDATA = Key<Events>("EVENTDATA")
        val TYPE = Key<Int>("TYPE")

        fun getAllMixinsAndSuperClasses(obj: Extclass?): List<String> {
            val allMixinsAndSuperClasses = LinkedHashSet<String>()
            obj?.name?.let { allMixinsAndSuperClasses.add(it) }
            collectMixinsAndSuperClasses(obj, allMixinsAndSuperClasses)
            return ArrayList(allMixinsAndSuperClasses)
        }

        private fun collectMixinsAndSuperClasses(obj: Extclass?, allMixinsAndSuperClasses: MutableSet<String>) {
            if (obj == null) {
                return
            }

            // 添加当前对象的名称
            obj.name?.let { allMixinsAndSuperClasses.add(it) }

            // 添加当前对象的mixins
            obj.mixins?.let { allMixinsAndSuperClasses.addAll(it) }

            // 递归处理父类
            val parentClassName = obj.extendsClass
            if (!parentClassName.isNullOrEmpty()) {
                val parentObj = Load.getExtclassByName(parentClassName)
                collectMixinsAndSuperClasses(parentObj, allMixinsAndSuperClasses)
            }
        }
    }

    fun getSuperElementBuilder(): List<LookupElementBuilder> {
        val ret = mutableListOf<LookupElementBuilder>()
        val classList = getAllMixinsAndSuperClasses(this)
        for (className in classList) {
            //System.out.println(className);
            Load.getExtclassByName(className)?.getLookupElementBuilder()?.let { ret.addAll(it) }
        }
        return ret
    }

    fun getLookupElementBuilder(): List<LookupElementBuilder> {
        val lookupElements = mutableListOf<LookupElementBuilder>()

        config?.forEach { cfg ->
            if (cfg.visibility != 0) {
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                return@forEach
            }

            var builder = LookupElementBuilder.create(cfg.name)
                .withTypeText("Property")
                .withIcon(AllIcons.General.Gear)
                .withTailText(" " + cfg.valueType.joinToString(", "), true)
            if (cfg.readOnly) {
                builder = builder.withTailText(" (readonly)", true)
            }
            lookupElements.add(builder)
        }

        properties?.forEach { prop ->
            if (prop.visibility != 0) {
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                return@forEach
            }
            var builder = LookupElementBuilder.create(prop.name)
                .withTypeText("Property")
                .withIcon(AllIcons.Nodes.Property)
                .withTailText(" " + prop.valueType.joinToString(", "), true)
            if (prop.readOnly) {
                builder = builder.withTailText(" (readonly)", true)
            }
            lookupElements.add(builder)
        }

        // 添加方法
        methods?.forEach { method ->
            if (method.visibility != 0) {
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                return@forEach
            }
            val e = LookupElementBuilder.create(method.name)
                .withTypeText("Method")
                .withIcon(AllIcons.Nodes.Method)
            lookupElements.add(e)
        }

        // 添加事件
        events?.forEach { evt ->
            if (evt.name == "afterrender") {
                val a = ""
            }
            val e = LookupElementBuilder.create(evt.name)
                .withTypeText("Event")
                .withIcon(AllIcons.Actions.Lightning)
            e.putUserData(IS_EVENT, "")
            e.putUserData(EVENTDATA, evt)
            lookupElements.add(e)
        }

        return lookupElements
    }

    private fun generateLookupElements() {
        // 清空现有的lookupElementsByPrefix
        lookupElementsByPrefix.clear()

        //获得全部上级
        val lookupElements = getSuperElementBuilder()

        val groupedLookupElements = HashMap<Char, MutableList<LookupElementBuilder>>()
        //循环所有元素
        for (element in lookupElements) {
            val prefix = if (element.lookupString.isEmpty()) '#' else element.lookupString[0]

            // 使用computeIfAbsent初始化数组并添加元素
            groupedLookupElements.computeIfAbsent(prefix) { ArrayList() }.add(element)
        }

        for ((key, value) in groupedLookupElements) {
            lookupElementsByPrefix[key] = value.toTypedArray()
        }
    }

    // 获取指定前缀的代码提示项
    fun getLookupElementsByPrefix(prefix: Char, context: ProcessingContext): Array<LookupElementBuilder> {
        if (lookupElementsByPrefix.isEmpty()) {
            this.generateLookupElements()
        }
        return lookupElementsByPrefix[prefix] ?: emptyArray()
    }

    /*
    fun getAllMethods(): List<Methods> {
        // 使用 LinkedHashSet 保持插入顺序且不重复
        val allMethods = LinkedHashSet(methods)

        // 获取所有父类和混入类
        val classList = getAllMixinsAndSuperClasses(this)
        for (className in classList) {
            val superClass = Load.getExtclassByName(className)
            if (superClass != null) {
                // 将父类和混入类的方法添加到集合中
                allMethods.addAll(superClass.methods)
            }
        }
        return ArrayList(allMethods)
    }
     */
}
