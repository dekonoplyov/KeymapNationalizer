package com.dekonoplyov

import java.awt.event.KeyEvent
import javax.swing.KeyStroke

typealias KeyCode = Int
typealias Replacement = Pair<KeyCode, KeyStroke>

// line should be trimmed and lowerCased
fun parseReplacement(line: String): Replacement? {
    val fromTo = line.split(" with ")
    if (fromTo.size != 2) {
        return null
    }
    val from = fromTo[0].trim()
    if (from.length != 1) {
        return null
    }
    val keyCode = getExtendedKeyCodeForChar(from[0].toInt())
    if (keyCode == KeyEvent.VK_UNDEFINED) {
        return null
    }

    val keyStroke = parseKeyStroke(fromTo[1].trim()) ?: return null

    return keyCode to keyStroke
}


// lower cased string
private fun parseKeyStroke(to: String): KeyStroke? {
    val modsKeyCode = to.split(" ")
    if (modsKeyCode.isEmpty() || modsKeyCode.last().length != 1) {
        return null
    }
    val keyCode = getExtendedKeyCodeForChar(modsKeyCode.last()[0].toInt())

    val tokens = modsKeyCode.dropLast(1).toMutableSet()
    var mods = 0
    if (tokens.contains("shift")) {
        mods = mods or KeyEvent.SHIFT_DOWN_MASK
        tokens.remove("shift")
    }
    if (tokens.contains("ctrl")) {
        mods = mods or KeyEvent.CTRL_DOWN_MASK
        tokens.remove("ctrl")
    }
    if (tokens.contains("meta")) {
        mods = mods or KeyEvent.META_DOWN_MASK
        tokens.remove("meta")
    }
    if (tokens.contains("alt")) {
        mods = mods or KeyEvent.ALT_DOWN_MASK
        tokens.remove("alt")
    }

    if (tokens.isEmpty()) {
        return KeyStroke.getKeyStroke(keyCode, mods)
    }

    return null
}