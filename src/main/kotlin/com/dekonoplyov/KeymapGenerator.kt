package com.dekonoplyov

import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.keymap.ex.KeymapManagerEx
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

fun keyToText(key: Int): String {
    if (key == KeyEvent.VK_DEAD_GRAVE) {
        return "`"
    }
    return KeymapUtil.getKeyText(key)
}

fun replacementText(replacements: Map<Int, Int>): String {
    val s = StringBuilder()

    for (r in replacements) {
        s.append("${keyToText(r.key)} with ${keyToText(r.value)}\n")
    }

    return s.toString()
}

fun generateKeymap(replacements: Map<Int, Int>) {
    val keymapManager = KeymapManager.getInstance()
    val activeKeymap = keymapManager.activeKeymap
    val nationalKeymap = activeKeymap.deriveKeymap(activeKeymap.name + " with national support")

    for (actionId in nationalKeymap.actionIdList) {
        for (shortcut in nationalKeymap.getShortcuts(actionId)) {
            if (shortcut !is KeyboardShortcut) {
                continue
            }

            var shouldMerge = replacements.containsKey(shortcut.firstKeyStroke.keyCode)
            shouldMerge = shouldMerge or replacements.containsKey(shortcut.secondKeyStroke?.keyCode)

            if (shouldMerge) {
                val merged = merge(shortcut, replacements)
                nationalKeymap.removeShortcut(actionId, shortcut)
                nationalKeymap.addShortcut(actionId, merged)
            }
        }
    }
    (keymapManager as KeymapManagerEx?)?.schemeManager?.addScheme(nationalKeymap)
    (keymapManager as KeymapManagerEx).activeKeymap = nationalKeymap
}

private fun merge(shortcut: KeyboardShortcut, replacements: Map<Int, Int>): KeyboardShortcut {
    if (shortcut.secondKeyStroke == null) {
        return KeyboardShortcut(merge(shortcut.firstKeyStroke, replacements), null)
    }
    return KeyboardShortcut(merge(shortcut.firstKeyStroke, replacements),
            merge(shortcut.secondKeyStroke!!, replacements))
}

private fun merge(stroke: KeyStroke, replacements: Map<Int, Int>): KeyStroke {
    val replacement = replacements[stroke.keyCode] ?: return stroke
    return KeyStroke.getKeyStroke(replacement, stroke.modifiers, stroke.isOnKeyRelease)
}
