package com.dekonoplyov

import java.awt.event.KeyEvent
import javax.swing.KeyStroke

typealias KeyCode = Int
typealias Replacement = Pair<KeyCode, KeyStroke>

// line should be StringProcessor.process(line)
fun parseReplacement(line: String): Replacement {
    val fromTo = line.split(" with ")
    if (fromTo.size != 2) {
        throw RuntimeException("Failed to parse: \"$line\"")
    }
    val from = fromTo[0]
    if (from.length != 1) {
        throw RuntimeException("Failed to parse key: \"$from\"")
    }
    val keyCode = getExtendedKeyCodeForChar(from[0].code)
    if (keyCode == KeyEvent.VK_UNDEFINED) {
        throw RuntimeException("Failed to parse key: \"$from\"")
    }

    val keyStroke = parseKeyStroke(fromTo[1])

    return keyCode to keyStroke
}

// line should be StringProcessor.process(line)
fun parseKeyStroke(to: String): KeyStroke {
    val modsKeyCode = to.split(" ")
    if (modsKeyCode.isEmpty() || modsKeyCode.last().length != 1) {
        throw RuntimeException("Failed to parse: \"$to\"")
    }
    val keyCode = getExtendedKeyCodeForChar(modsKeyCode.last()[0].code)
    if (keyCode == KeyEvent.VK_UNDEFINED) {
        throw RuntimeException("Failed to parse key: \"${modsKeyCode.last()}\"")
    }

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
    throw RuntimeException("Failed to parse: \"${tokens.joinToString(" ")}\"")
}