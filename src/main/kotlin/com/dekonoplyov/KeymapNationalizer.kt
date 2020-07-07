package com.dekonoplyov

// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.DumbAwareAction
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
    var replacementPreview = initEditor()
    var replacements = mutableMapOf<KeyCode, KeyStroke>()

    override fun actionPerformed(e: AnActionEvent) {
        val generateKeymapForText = "Generate keymap for"
        val replaceText = "Replace"
        val some = Some()
        val values = some.supportedLocales.values.toArray(emptyArray())
        replacementPreview.text = replacementText(some.getReplacements())

        dialog(
                title = "Generate national keymap",
                panel = panel {
                    row {
                        label(generateKeymapForText)
                        comboBox(DefaultComboBoxModel(values), some::chosenLang)
                                .applyToComponent {
                                    addItemListener {
                                        if (it.stateChange == ItemEvent.SELECTED) {
                                            some.chosenLang = it.item as String
                                            replacementPreview.text = replacementText(some.getReplacements())
                                        }
                                    }
                                }
                    }
                    row {
                        label(replaceText)
                    }
                    row {
                        replacementPreview()
                    }
                }
        ).showAndGet().let {
            if (it) {
                generateKeymap(replacements)
            }
        }
    }

    private fun initEditor(): EditorTextField {
        val document = EditorFactory.getInstance().createDocument("")
        val replacementPreview = EditorTextField(document, null, FileTypes.PLAIN_TEXT,
                false, false)
        replacementPreview.preferredSize = Dimension(300, 350)
        replacementPreview.addSettingsProvider { editor ->
            editor.setVerticalScrollbarVisible(true)
            editor.setHorizontalScrollbarVisible(true)
            editor.settings.additionalLinesCount = 2
        }
        replacementPreview.addDocumentListener(object : DocumentListener {
            override fun documentChanged(e: DocumentEvent) {
                update()
            }
        })

        return replacementPreview
    }

    private fun update(): Boolean {
        val editor = replacementPreview.editor ?: return false
        var containsErrors = false
        replacements = mutableMapOf()
        replacementPreview.text
                .split("\n")
                .forEachIndexed { index, s ->
                    val processed = StringProcessor.process(s)
                    if (processed.isEmpty()) return@forEachIndexed
                    val replacement = parseReplacement(processed)
                    if (replacement == null) {
                        containsErrors = true
                        editor.markupModel.addLineHighlighter(index,
                                HighlighterLayer.ERROR,
                                editor.colorsScheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES))
                        return@forEachIndexed
                    } else {
                        replacements[replacement.first] = replacement.second
                    }
                }
        if (!containsErrors) {
            editor.markupModel.removeAllHighlighters()
        }
        return containsErrors
    }
}


