package com.daima.exthelp.ext.comple.mod

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import java.sql.Connection
import java.sql.DriverManager

// 继承自 modbase 基类
class Dbm : modbase() {

    // 数据库连接参数
    private val jdbcUrl = "jdbc:mysql://192.168.10.60:3306/wangcunlu_his"
    private val username = "wangcunlu"
    private val password = "wangcunlu!60NEW"

    // 用于指示数据是否已加载的标志
    private var isDataLoaded = false

    // 缓存的表结构信息，包含表名和表注释
    private val tableComments: MutableMap<String, String?> = mutableMapOf()

    // 手动指定的二级类目和名称的 Map
    private val TableClassMap: MutableMap<String, String> = mutableMapOf(
        "mzd" to "门诊",
        "mzs" to "门诊",
        "jcd" to "检查",
        "jcs" to "检查",
        "sfs" to "收费项目",
        "xt" to "系统",
        "yfd" to "药房",
        "yps" to "药品",
        "zyd" to "住院",
        "zys" to "住院",
    )
    private val FuncMap: MutableMap<String, String> = mutableMapOf(
        "newStore" to "创建空Store",
        "newTreeStore" to "创建空TreeStore，需要传ParentId",
    )
    private val remarkMap: MutableMap<String, String> = mutableMapOf(
        "JCS_BWDYFF" to "检查部位对应方法表",
        "JCS_JCBW" to "检查部位表",
        "JCS_JCFF" to "检查方法表",
        "MZD_BRJZK" to "病人就诊卡信息表",
        "MZD_BRJZXX" to "门诊病人就诊信息表",
        "MZD_BRXX" to "病人基本信息表",
        "MZD_GHSF" to "门诊挂号收费表",
        "MZD_GHSFMX" to "门诊挂号收费明细表",
        "MZD_KSPBXX" to "科室排班信息表",
        "MZD_MZCF" to "门诊处方表",
        "MZD_MZCFMX" to "门诊处方明细表",
        "MZD_MZSK" to "门诊收款表",
        "MZD_MZSKMX" to "门诊收款明细表",
        "MZD_MZZD" to "门诊诊断表",
        "MZD_SFXMFJHC" to "门诊收费项目附加耗材表",
        "MZD_SFXMFJHCMX" to "门诊收费项目附加耗材明细表",
        "MZD_SSSQD" to "门诊手术申请单表",
        "MZD_SSSQDMX" to "门诊手术申请单明细表",
        "MZD_SYCFZX" to "门诊输液处方执行表",
        "MZD_YJSQD" to "门诊医技申请单表",
        "MZD_YJSQDMX" to "门诊医技申请单明细表",
        "MZD_YSPBXX" to "医生排班信息表",
        "MZS_CYZD" to "门诊常用诊断表",
        "MZS_GHLX" to "挂号类型表",
        "MZS_GHLXFY" to "挂号类型费用表",
        "MZS_SSSQDMB" to "门诊手术申请单模板表",
        "MZS_SSSQDMBMX" to "门诊手术申请单模板明细表",
        "SFS_DYHC" to "收费项目对应耗材表",
        "SFS_XM" to "收费项目表",
        "SFS_XMFL" to "收费项目分类表",
        "SFS_XMZXKS" to "收费项目执行科室表",
        "XT_BMGZXZ" to "部门工作性质表",
        "XT_BMSSJXX" to "部门手术间信息表",
        "XT_BMXX" to "部门信息表",
        "XT_CFMB" to "处方模板表",
        "XT_CFMBMX" to "处方模板明细表",
        "XT_CS" to "系统参数表",
        "XT_CSFL" to "系统参数分类表",
        "XT_CSXM" to "系统参数项目表",
        "XT_CS_UPDATE" to "",
        "XT_GROUPQX" to "工作组权限表",
        "XT_GROUPRY" to "工作组人员对应表",
        "XT_GYSXX" to "供应商信息表",
        "XT_GZZGROUP" to "工作站工作组表",
        "XT_GZZLX" to "工作站类型表",
        "XT_GZZRY" to "工作站人员对应表",
        "XT_GZZZJ" to "工作站组件表",
        "XT_HMGZ" to "号码规则表",
        "XT_HMGZFL" to "号码规则分类表",
        "XT_JBFL" to "疾病分类表",
        "XT_JBXX" to "疾病信息表",
        "XT_JCZL" to "基础资料表",
        "XT_JCZLFL" to "基础资料分类表",
        "XT_JCZLSX" to "基础资料辅助属性表",
        "XT_KSBQDY" to "科室病区对应表",
        "XT_RYBM" to "人员部门表",
        "XT_RYGZXZ" to "人员工作性质表",
        "XT_RYQX" to "人员权限表",
        "XT_RYXX" to "人员信息表",
        "XT_RYZH" to "人员账号表",
        "XT_SCCJXX" to "生产厂家信息表",
        "XT_TOPCD" to "工作站顶部菜单表",
        "XT_XLCD" to "工作站下拉菜单表",
        "XT_XZQH" to "行政区划表",
        "XT_YJSQDMB" to "门诊医技申请单模板表",
        "XT_YJSQDMBMX" to "门诊医技申请单模板明细表",
        "XT_YWLX" to "业务类型表",
        "YFD_CFPFJL" to "处方批发记录表",
        "YFD_CFPFJLMX" to "处方批发记录明细表",
        "YFD_SLD" to "药品申领单表",
        "YFD_SLDMX" to "药品申领单明细表",
        "YFD_TKSQ" to "药品退库申请表",
        "YFD_TKSQMX" to "药品退库明细表",
        "YKD_BYD" to "药品拨药单表",
        "YKD_BYDMX" to "药品拨药单明细表",
        "YKD_KCCBJTZD" to "库存成本价调整单",
        "YKD_KCCBJTZDMX" to "库存成本价调整单明细表",
        "YKD_KCLSJTZD" to "库存零售价调整单",
        "YKD_KCLSJTZDMX" to "库存零售价调整单明细表",
        "YKD_KFQCSJ" to "库房期初数据表",
        "YKD_YPPDFA" to "药品盘点方案表",
        "YKD_YPPDFAMX" to "药品盘点方案明细表",
        "YPD_CRK" to "药品出入库表",
        "YPD_CRKMX" to "药品出入库明细表",
        "YPD_KCMX" to "药品库存明细表",
        "YPS_FL" to "药品目录分类表",
        "YPS_ML" to "药品目录表",
        "ZYD_BASY" to "病案首页表",
        "ZYD_BASY_SS" to "病案首页手术表",
        "ZYD_BASY_XYZD" to "病案首页西医诊断表",
        "ZYD_BASY_ZYZD" to "病案首页中医诊断表",
        "ZYD_BASY_ZZJH" to "病案首页重症监护表",
        "ZYD_GGJCJLD" to "光感监测记录单表",
        "ZYD_HLJL" to "住院护士护理记录表",
        "ZYD_HLJLMX" to "住院护士护理记录明细表",
        "ZYD_HZSQD" to "会诊申请单表",
        "ZYD_HZSQDMX" to "会诊申请单明细表",
        "ZYD_MZZYSQ" to "门诊住院申请表",
        "ZYD_PSYZ" to "住院派生医嘱表",
        "ZYD_PSYZMX" to "住院派生医嘱明细表",
        "ZYD_SHXYJLD" to "术后血压记录单表",
        "ZYD_XTJLD" to "血糖记录单表",
        "ZYD_YJSQD" to "住院医技申请单表",
        "ZYD_YJSQDMX" to "住院医技申请单明细表",
        "ZYD_YZDYJL" to "住院医嘱打印记录表",
        "ZYD_YZZXJL" to "住院医嘱执行记录表",
        "ZYD_YZZXJLMX" to "住院医嘱执行记录明细表",
        "ZYD_ZYBRCWXX" to "住院病人床位信息表",
        "ZYD_ZYBRCZJL" to "住院病人操作记录表",
        "ZYD_ZYBRGDFYTCMX" to "住院病人固定费用套餐明细表",
        "ZYD_ZYBRZKJL" to "住院病人转科记录表",
        "ZYD_ZYCF" to "住院处方表",
        "ZYD_ZYCFMX" to "住院处方明细表",
        "ZYD_ZYDJXX" to "住院登记信息表",
        "ZYD_ZYJSD" to "住院结算单表",
    )
    // 从数据库加载数据的方法
    private fun loadData() {
        Class.forName("com.mysql.cj.jdbc.Driver")
        if (isDataLoaded) return // 防止重复加载数据
        val connection: Connection = DriverManager.getConnection(jdbcUrl, username, password)
        try {
            val databaseMetaData = connection.metaData
            val tablesResultSet = databaseMetaData.getTables(null, null, "%", arrayOf("TABLE"))

            while (tablesResultSet.next()) {
                val tableName = tablesResultSet.getString("TABLE_NAME")
                val tableComment = this.remarkMap[tableName];//tablesResultSet.getString("REMARKS") // 获取表注释
                // 将表名和注释存储到缓存中
                tableComments[tableName] = tableComment
            }
            tablesResultSet.close()
            isDataLoaded = true // 数据加载完成后设置标志为真
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }

    fun Run(psi: PsiElement): List<LookupElement> {
        loadData() // 确保数据已加载
        val continuousText = getContinuousText(psi)

        // 判断是否是以 "Dbm" 开头
        if (!continuousText.startsWith("Dbm")) {
            return listOf()
        }

        // 将 continuousText 变成数组
        val parts = continuousText.split('.').map { it.trim() }

        // 判断数组长度为1时，执行 tableclass
        return when (parts.size) {
            1 -> tableclass()
            2 -> {
                val lastPart = parts.last().lowercase()
                filterAndGenerateLookupElements(lastPart)
            }
            3->funclist()
            else -> listOf()
        }
    }

    private fun filterAndGenerateLookupElements(prefix: String): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()
        for ((key, v) in tableComments) {
            val lowerKey = key.lowercase()
            if (lowerKey.startsWith(prefix)) {
                // 将下划线右边的部分处理为首字母大写
                val formattedName = key.split('_').getOrNull(1)?.lowercase()?.replaceFirstChar { it.uppercase() } ?: continue

                val lookupElement = LookupElementBuilder.create(formattedName)
                    .withTypeText(v)
                    .withIcon(AllIcons.Nodes.DataTables) // 或使用其他合适的图标
                lookupList.add(PrioritizedLookupElement.withPriority(lookupElement,1000000000.0))
            }
        }
        return lookupList
    }
    // 生成基于 TableClassMap 的代码提示元素
    private fun tableclass(): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()
        // 遍历 TableClassMap，生成 LookupElementBuilder
        for ((key, value) in TableClassMap) {
            var lookupElement = LookupElementBuilder.create(key)
                .withTypeText(value) // 将二级类目名称作为类型文本显示
                .withIcon(AllIcons.Gutter.DataSchema) // 设置图标
                .withInsertHandler(DotInsertHandler) // 设置 InsertHandler
            lookupList.add(PrioritizedLookupElement.withPriority(lookupElement,1000000000.0))
        }

        return lookupList
    }
    // 生成基于 TableClassMap 的代码提示元素
    private fun funclist(): List<LookupElement> {
        val lookupList = mutableListOf<LookupElement>()

        // 遍历 TableClassMap，生成 LookupElementBuilder
        for ((key, value) in FuncMap) {
            var lookupElement = LookupElementBuilder.create(key)
                .withTypeText(value) // 将二级类目名称作为类型文本显示
                .withIcon(AllIcons.Nodes.Function) // 设置图标
                .withInsertHandler(DotInsertHandler) // 设置 InsertHandler
            lookupList.add(PrioritizedLookupElement.withPriority(lookupElement,1000000000.0))
        }

        return lookupList
    }
    // 检查表名是否符合条件的方法
    private fun isEligibleTableName(tableName: String): Boolean {
        // 确保只有一个下划线，并且所有字母均为大写
        val parts = tableName.split('_')
        return parts.size == 2 && parts.all { it.isNotEmpty() && it.all { char -> char.isUpperCase() } }
    }

    // 格式化表名的方法
    private fun formatTableName(tableName: String): String {
        val parts = tableName.split('_')
        if (parts.size == 2) {
            val left = parts[0].lowercase()
            val right = parts[1].lowercase().replaceFirstChar { it.uppercase() }
            return "$left.$right"
        }
        return tableName // 如果分割结果不正确，返回原名
    }

    // 自定义 InsertHandler，选择提示项后自动插入 `.` 并触发新提示
    private object DotInsertHandler : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val document = context.document
            val editor = context.editor

            // 插入 `.` 并移动光标
            document.insertString(context.selectionEndOffset, ".")
            context.commitDocument()

            // 移动光标到 `.` 后面
            editor.caretModel.moveToOffset(context.selectionEndOffset )

            // 触发新一轮代码提示
            AutoPopupController.getInstance(context.project).autoPopupMemberLookup(editor, null)
        }
    }
}