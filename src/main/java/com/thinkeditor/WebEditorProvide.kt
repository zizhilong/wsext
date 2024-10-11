package com.thinkeditor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.Element

class CustomWebEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.extension == "txml"
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return WebEditor(file)
    }

    override fun getEditorTypeId(): String = "custom-web-editor"
    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    //override fun readState(sourceElement: Element, project: Project, file: VirtualFile): FileEditorState? = null
    override fun writeState(state: FileEditorState, project: Project, targetElement: Element) {}
}