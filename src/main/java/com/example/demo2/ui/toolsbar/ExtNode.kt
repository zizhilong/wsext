package com.example.demo2.ui.toolsbar

import javax.swing.tree.DefaultMutableTreeNode



class ExtNode(val label: String, val tooltip: String) : DefaultMutableTreeNode() {

    init {
        userObject = label
    }

    // 不显式定义 getter 方法，因为 Kotlin 会自动生成
}