package com.thinkeditor

import CustomLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class TxmlFileType : LanguageFileType(CustomLanguage) {
    override fun getName(): String = "ThinkEditor XML File"
    override fun getDescription(): String = "信译窗口文件"
    override fun getDefaultExtension(): String = "txml"
    override fun getIcon(): Icon? = null // 这里可以设置图标，也可以设置为 null
}