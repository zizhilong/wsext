package com.daima.exthelp.ext.extclass.extview

import com.daima.exthelp.Exp.PExp.Parser
import com.daima.exthelp.Tools.ExpHelper
import com.daima.exthelp.Tools.StringHelper
import com.daima.exthelp.ext.interfaces.CODE_HELP_KEY
import com.daima.exthelp.ext.interfaces.CodeHelpInterface
import com.daima.exthelp.extdata.Load
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext

//用于输入Xtype打双引号
val doubleBracketInsertHandler = InsertHandler<LookupElement> { context, _ ->
    val document = context.document
    val startOffset = context.startOffset
    val tailOffset = context.tailOffset

    // 插入左括号
    document.insertString(startOffset, "\"")
    // 插入右括号
    document.insertString(tailOffset + 1, "\"")
    // 可选: 移动光标到括号之间
    context.editor.caretModel.moveToOffset(tailOffset + 1)
    // 提交更改
    context.commitDocument()
}
// 描述一个View下边的Class并实现 CodeHelp 接口
class ExtViewItemClass(
     val jsObject: JSObjectLiteralExpression,
     //val code:=
) : CodeHelpInterface {
    // 存储下级对象的数组
    private val subItems: MutableList<ExtViewItemClass> = mutableListOf()

    init {
        // 初始化时尝试创建下级对象
        initializeSubItems()
    }
    // 初始化子项
    private fun initializeSubItems() {
        jsObject.putUserData(CODE_HELP_KEY, this)
        var itemProperty=this.getObjProperty(jsObject,"items")
        // 检查 item 属性是否是数组
        if (itemProperty?.value is JSArrayLiteralExpression) {
            val itemArray = itemProperty.value as JSArrayLiteralExpression
            // 遍历数组中的每个元素
            for (element in itemArray.expressions) {
                if (element is JSObjectLiteralExpression) {
                    val subItem = ExtViewItemClass(element)
                    subItems.add(subItem)
                }
            }
        }
        //判断自己下边是否有属性listeners:
        //处理自己的事件绑定
        var listeners=this.getObjProperty(jsObject,"listeners")
        if (listeners?.value is JSObjectLiteralExpression) {
            var listenerobj=listeners.value as JSObjectLiteralExpression
            var xtype=this.getXtypeName()
            if(xtype==""){
                val xtypeProperty=jsObject.findProperty("extend")
                val classname = xtypeProperty?.value?.text
                if (classname != null) {
                    xtype=classname
                }
            }

            ExtViewListeners(listenerobj,xtype)
        }
    }
    // 获取所有子项
    fun getSubItems(): List<ExtViewItemClass> {
        return subItems
    }
    // 实现 CodeHelp 接口中的方法
    override fun RunIng():Boolean{
        return true
    }

// 假设 getCodeSuggestions 是用于代码补全的函数
    override fun getCodeSuggestions(psiElement: PsiElement, context: ProcessingContext): List<LookupElementBuilder> {
        ExpHelper.logPsiHierarchy(psiElement)
        //尝试确定是否修改的xtype属性
        val xtypeParser = Parser("LY{name xtype}")
        var ret = xtypeParser.RunExp(psiElement)
        if(ret==null){
            ret=Parser("LRY{name xtype}").RunExp(psiElement)
        }
        // 创建一个存储建议的列表
        val lookupElements = mutableListOf<LookupElementBuilder>()
        //如果是直接改的xtype属性
        if (ret != null) {
            // 遍历 XType 映射并添加到建议列表中
            for ((key, value) in getXtype()) {
                lookupElements.add(
                    LookupElementBuilder.create(key)
                        .withTypeText(value)
                        .withIcon(AllIcons.Nodes.Class)
                        .withInsertHandler(doubleBracketInsertHandler)
                )
            }
            return lookupElements
        }
        //如果不是修改Xtype属性

        val obj = Parser("LREO").RunExp(psiElement)
        //如果属于属性设定
        if(obj!=null){
            val extclass = Load.getExtclassByName(this.getXtypeName()) ?: return listOf()
            return extclass.getLookupElementsByPrefix(this.getPrefix(psiElement),"Config")
        }
        return listOf()
    }
    fun getXtypeName():String{
        val xtypeProperty=jsObject.findProperty("xtype")
        val classname = xtypeProperty?.value?.text
        if (classname == null) {return ""}
        return StringHelper.trimQuotes(classname)
    }

    fun getXtype():MutableMap<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()
        map["component"] = "命名空间";
        map["box"] = "命名空间";
        map["editor"] = "编辑器";
        map["image"] = "图片";
        map["imagecomponent"] = "图片";
        map["loadmask"] = "模式化的浮动组件";
        map["progress"] = "简单的进度条部件";
        map["progressbarwidget"] = "简单的进度条部件";
        map["progressbar"] = "更新的进度条组件";
        map["widget"] = "轻量级组件";
        map["button"] = "按钮";
        map["cycle"] = "按钮循环选择";
        map["segmentedbutton"] = "分段按钮";
        map["splitbutton"] = "分裂按钮";
        map["calendar"] = "日历面板";
        map["cartesian"] = "笛卡尔图表";
        map["chart"] = "图表";
        map["polar"] = "极坐标图表";
        map["spacefilling"] = "填充图表";
        map["axis"] = "坐标轴";
        map["axis3d"] = "3D坐标轴";
        map["interaction"] = "图表交互";
        map["chartnavigator"] = "图表导航器";
        map["buttongroup"] = "按钮组";
        map["container"] = "容器";
        map["viewport"] = "视口";
        map["dashboard"] = "仪表板";
        map["draw"] = "绘图容器";
        map["surface"] = "绘图表面";
        map["flash"] = "Flash组件";
        map["checkboxgroup"] = "复选框组";
        map["fieldcontainer"] = "字段容器";
        map["fieldset"] = "字段集";
        map["label"] = "标签";
        map["form"] = "表单面板";
        map["radiogroup"] = "单选框组";
        map["field"] = "字段";
        map["checkboxfield"] = "复选框字段";
        map["checkbox"] = "复选框";
        map["combobox"] = "组合框";
        map["combo"] = "组合框";
        map["datefield"] = "日期字段";
        map["displayfield"] = "显示字段";
        map["filefield"] = "文件字段";
        map["fileuploadfield"] = "文件上传字段";
        map["filebutton"] = "文件按钮";
        map["hiddenfield"] = "隐藏字段";
        map["hidden"] = "隐藏字段";
        map["htmleditor"] = "HTML编辑器";
        map["numberfield"] = "数字字段";
        map["pickerfield"] = "选择字段";
        map["radiofield"] = "单选框字段";
        map["radio"] = "单选框";
        map["spinnerfield"] = "微调字段";
        map["tagfield"] = "标签字段";
        map["textfield"] = "文本字段";
        map["textareafield"] = "文本区域字段";
        map["textarea"] = "文本区域";
        map["timefield"] = "时间字段";
        map["triggerfield"] = "触发字段";
        map["trigger"] = "触发字段";
        map["froalaeditor"] = "Froala编辑器";
        map["froalaeditorfield"] = "Froala编辑器字段";
        map["celleditor"] = "单元格编辑器";
        map["gridpanel"] = "表格面板";
        map["grid"] = "表格";
        map["roweditor"] = "行编辑器";
        map["roweditorbuttons"] = "行编辑器按钮";
        map["actioncolumn"] = "操作列";
        map["booleancolumn"] = "布尔列";
        map["checkcolumn"] = "检查列";
        map["gridcolumn"] = "表格列";
        map["datecolumn"] = "日期列";
        map["groupscolumn"] = "分组列";
        map["numbercolumn"] = "数字列";
        map["rownumberer"] = "行号列";
        map["templatecolumn"] = "模板列";
        map["widgetcolumn"] = "小部件列";
        map["headercontainer"] = "表头容器";
        map["groupingpanelcolumn"] = "分组面板列";
        map["groupingpanel"] = "分组面板";
        map["propertygrid"] = "属性表格";
        map["columnsplitter"] = "列分隔器";
        map["treelist"] = "树形列表";
        map["treelistitem"] = "树形列表项";
        map["menubar"] = "菜单栏";
        map["menucheckitem"] = "菜单复选项";
        map["colormenu"] = "颜色选择菜单";
        map["datemenu"] = "日期选择菜单";
        map["menuitem"] = "菜单项";
        map["menu"] = "菜单";
        map["menuseparator"] = "菜单分隔符";
        map["header"] = "面板标题";
        map["panel"] = "面板";
        map["tablepanel"] = "表格面板";
        map["title"] = "面板标题";
        map["tool"] = "工具";
        map["colorpicker"] = "颜色选择器";
        map["datepicker"] = "日期选择器";
        map["monthpicker"] = "月份选择器";
        map["timepicker"] = "时间选择器";
        map["pivotgrid"] = "数据透视表格";
        map["mzpivotgrid"] = "数据透视表格";
        map["pivotd3container"] = "数据透视D3容器";
        map["pivotheatmap"] = "数据透视热力图";
        map["pivottreemap"] = "数据透视树图";
        map["pivotconfigfield"] = "数据透视配置字段";
        map["pivotconfigcontainer"] = "数据透视配置容器";
        map["pivotconfigpanel"] = "数据透视配置面板";
        map["bordersplitter"] = "边界分隔器";
        map["splitter"] = "分隔器";
        map["multislider"] = "多滑块";
        map["slider"] = "滑块";
        map["sliderfield"] = "滑块字段";
        map["slidertip"] = "滑块提示";
        map["sliderwidget"] = "滑块小部件";
        map["sparklinebar"] = "条形图";
        map["sparkline"] = "迷你图";
        map["sparklinebox"] = "箱形图";
        map["sparklinebullet"] = "子弹图";
        map["sparklinediscrete"] = "离散图";
        map["sparklineline"] = "折线图";
        map["sparklinepie"] = "饼图";
        map["sparklinetristate"] = "三态图";
        map["tabbar"] = "标签栏";
        map["tabpanel"] = "标签面板";
        map["tab"] = "标签";
        map["quicktip"] = "快速提示";
        map["tip"] = "提示";
        map["tooltip"] = "工具提示";
        map["breadcrumb"] = "面包屑导航";
        map["tbfill"] = "工具栏填充";
        map["tbitem"] = "工具栏项";
        map["pagingtoolbar"] = "分页工具栏";
        map["tbseparator"] = "工具栏分隔符";
        map["tbspacer"] = "工具栏间隔";
        map["tbtext"] = "工具栏文本项";
        map["toolbar"] = "工具栏";
        map["treecolumn"] = "树列";
        map["treepanel"] = "树面板";
        map["treeview"] = "树视图";
        map["explorer"] = "资源管理器";
        map["gmappanel"] = "谷歌地图面板";
        map["uxiframe"] = "IFrame扩展";
        map["treepicker"] = "树选择器";
        map["colorbutton"] = "颜色按钮";
        map["colorpickercolormap"] = "颜色选择器地图";
        map["colorpickercolorpreview"] = "颜色选择器预览";
        map["colorfield"] = "颜色字段";
        map["colorselector"] = "颜色选择器";
        map["colorpickerslider"] = "颜色选择器滑块";
        map["colorpickerslideralpha"] = "颜色选择器透明度滑块";
        map["colorpickersliderhue"] = "颜色选择器色调滑块";
        map["colorpickerslidersaturation"] = "颜色选择器饱和度滑块";
        map["colorpickerslidervalue"] = "颜色选择器明度滑块";
        map["desktop"] = "桌面";
        map["taskbar"] = "任务栏";
        map["trayclock"] = "托盘时钟";
        map["video"] = "视频";
        map["wallpaper"] = "壁纸";
        map["eventrecordermanager"] = "事件记录管理器";
        map["itemselectorfield"] = "项目选择器字段";
        map["itemselector"] = "项目选择器";
        map["multiselectfield"] = "多选字段";
        map["multiselect"] = "多选";
        map["searchfield"] = "搜索字段";
        map["gauge"] = "仪表";
        map["rating"] = "评分选择器";
        map["statusbar"] = "状态栏";
        map["boundlist"] = "绑定列表";
        map["multiselector"] = "多选择器";
        map["tableview"] = "表格视图";
        map["gridview"] = "表格视图";
        map["dataview"] = "数据视图";
        map["messagebox"] = "消息框";
        map["toast"] = "消息提示";
        map["window"] = "窗口";
        return map
    }
}