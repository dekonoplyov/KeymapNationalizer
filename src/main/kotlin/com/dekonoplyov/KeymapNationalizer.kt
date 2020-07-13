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
import com.intellij.ui.components.dialog
import com.intellij.ui.layout.applyToComponent
import com.intellij.ui.layout.panel
import com.intellij.util.containers.toArray
import java.awt.Dimension
import java.awt.event.ItemEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.KeyStroke

internal class KeymapNationalizer : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val predefined = Predefined()
        val replacementPreview = initEditor()

        val dialogue = dialog(
                title = "",
                panel = panel {
                    row {
                        label("Generate keymap for")
                        comboBox(DefaultComboBoxModel(predefined.values.keys.toArray(emptyArray())),
                                predefined::chosenLang)
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
        )

        val validator = DataValidator(replacementPreview, dialogue)
        replacementPreview.addDocumentListener(object : DocumentListener {
            override fun documentChanged(e: DocumentEvent) {
                validator.validate()
            }
        })
        replacementPreview.text = predefined.getReplacements()

        dialogue.showAndGet().let {
            val replacements = validator.validate()
            if (it && replacements != null) {
                generateKeymap(replacements)
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

        return replacementPreview
    }
}

class DataValidator(private val replacementPreview: EditorTextField,
                    private val dialogue: DialogWrapper) {
    fun validate(): Map<KeyCode, KeyStroke>? {
        var isOk = true
        val replacements = mutableMapOf<KeyCode, KeyStroke>()
        replacementPreview.text
                .split("\n")
                .forEachIndexed { index, s ->
                    val processed = StringProcessor.process(s)
                    if (processed.isEmpty()) return@forEachIndexed
                    try {
                        val replacement = parseReplacement(processed)
                        replacements[replacement.first] = replacement.second
                    } catch (e: RuntimeException) {
                        isOk = false
                        val editor = replacementPreview.editor ?: return@forEachIndexed
                        HintManagerImpl.getInstanceImpl().showErrorHint(editor, e.message!!)
                        editor.markupModel.addLineHighlighter(index,
                                HighlighterLayer.ERROR,
                                editor.colorsScheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES))
                        return@forEachIndexed
                    }
                }
        dialogue.isOKActionEnabled = isOk
        if (isOk) {
            replacementPreview.editor?.markupModel?.removeAllHighlighters()
            return replacements
        }
        return null
    }
}
