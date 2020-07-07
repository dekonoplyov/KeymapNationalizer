package com.dekonoplyov

// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorTextField
import com.intellij.ui.layout.applyToComponent
import com.intellij.ui.layout.panel
import com.intellij.util.containers.toArray
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.KeyStroke

internal class KeymapNationalizer : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val d = KeymapNationalizerDialog()
        d.showAndGet().let {
            if (it) {
                generateKeymap(d.replacements)
            }
        }
    }
}

class KeymapNationalizerDialog : DialogWrapper(null) {
    val predefined = Predefined()
    val values = predefined.values.keys.toArray(emptyArray())
    var replacementPreview = initEditor()
    var replacements = mutableMapOf<KeyCode, KeyStroke>()

    init {
        isModal = true

        init()
    }

    override fun createCenterPanel(): JComponent? {

        return panel {
            row {
                label("Generate keymap for")
                comboBox(DefaultComboBoxModel(values), predefined::chosenLang)
                        .applyToComponent {
                            addItemListener {
                                if (it.stateChange == ItemEvent.SELECTED) {
                                    predefined.chosenLang = it.item as String
                                    replacementPreview.text = predefined.getReplacements()
                                }
                            }
                        }
            }
            row {
                label("Replace")
            }
            row {
                replacementPreview()
            }
        }
    }

    private fun initEditor(): EditorTextField {
        val document = EditorFactory.getInstance().createDocument("")
        val replacementPreview = EditorTextField(document, null, FileTypes.PLAIN_TEXT,
                false, false)
        replacementPreview.preferredSize = Dimension(300, 280)
        replacementPreview.addSettingsProvider { editor ->
            editor.setVerticalScrollbarVisible(true)
            editor.setHorizontalScrollbarVisible(true)
            editor.settings.additionalLinesCount = 2
        }
        replacementPreview.addDocumentListener(object : DocumentListener {
            override fun documentChanged(e: DocumentEvent) {
                val isOkToErr = update()
                isOKActionEnabled = isOkToErr
            }
        })

        replacementPreview.text = predefined.getReplacements()

        return replacementPreview
    }

    private fun update(): Boolean {
        val editor = replacementPreview.editor ?: return true
        var isOk = true
        replacements = mutableMapOf()
        replacementPreview.text
                .split("\n")
                .forEachIndexed { index, s ->
                    val processed = StringProcessor.process(s)
                    if (processed.isEmpty()) return@forEachIndexed
                    try {
                        val  replacement = parseReplacement(processed)
                        replacements[replacement.first] = replacement.second
                    } catch (e: RuntimeException) {
                        isOk = false
                        HintManagerImpl.getInstanceImpl().showErrorHint(editor, e.message!!)
                        editor.markupModel.addLineHighlighter(index,
                                HighlighterLayer.ERROR,
                                editor.colorsScheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES))
                        return@forEachIndexed
                    }
                }
        if (isOk) {
            editor.markupModel.removeAllHighlighters()
        }
        return isOk
    }
}
