package com.daima.exthelp.Tools

import java.sql.Connection
import java.sql.DriverManager

object Dbdata {

    // 数据库连接参数
    private const val jdbcUrl = "jdbc:mysql://192.168.10.60:3306/wangcunlu_his"
    private const val username = "wangcunlu"
    private const val password = "wangcunlu!60NEW"

    // 用于指示数据是否已加载的标志
    private var isDataLoaded = false

    // 缓存的表结构信息，包含表名和表注释
    private val tableComments: MutableMap<String, String?> = mutableMapOf()

    // 缓存的列备注信息，包含列名和列注释
    private val columnComments: MutableMap<String, String?> = mutableMapOf()

    // 从数据库加载数据的方法
    fun loadData() {
        if (isDataLoaded) return // 防止重复加载数据

        Class.forName("com.mysql.cj.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(jdbcUrl, username, password)
        try {
            // 获取当前数据库名称
            val databaseName = connection.catalog

            // 获取表的备注信息
            val tableQuery = """
                SELECT TABLE_NAME, TABLE_COMMENT 
                FROM information_schema.tables 
                WHERE table_schema = ?
            """
            val tablePreparedStatement = connection.prepareStatement(tableQuery)
            tablePreparedStatement.setString(1, databaseName)
            val tableResultSet = tablePreparedStatement.executeQuery()

            while (tableResultSet.next()) {
                val tableName = tableResultSet.getString("TABLE_NAME")
                val tableComment = tableResultSet.getString("TABLE_COMMENT")
                tableComments[tableName] = tableComment
            }
            tableResultSet.close()

            // 获取列的备注信息
            val columnQuery = """
                SELECT COLUMN_NAME, COLUMN_COMMENT 
                FROM information_schema.columns 
                WHERE table_schema = ?
            """
            val columnPreparedStatement = connection.prepareStatement(columnQuery)
            columnPreparedStatement.setString(1, databaseName)
            val columnResultSet = columnPreparedStatement.executeQuery()

            while (columnResultSet.next()) {
                val columnName = columnResultSet.getString("COLUMN_NAME")
                val columnComment = columnResultSet.getString("COLUMN_COMMENT")
                // 只有在 map 中还未存在该列名时才存储
                columnComments.putIfAbsent(columnName, columnComment)
            }
            columnResultSet.close()

            isDataLoaded = true // 数据加载完成后设置标志为真
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }

    // 查询列名的备注，如果列名在不同表中存在，则返回第一个备注
    fun getColumnComment(columnName: String): String? {
        loadData()
        return columnComments[columnName]
    }

    // 查询表名的备注
    fun getTableComment(tableName: String): String? {
        loadData()
        return tableComments[tableName]
    }
}