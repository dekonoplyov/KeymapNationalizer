package com.dekonoplyov

import java.awt.event.KeyEvent

class Some {
    val supportedLocales = mapOf("de" to "German",
            "it" to "Italian",
            "cz" to "Czech",
            "ot" to "Other")

    var chosenLang = "German"


    fun getReplacements(): Map<Int, Int> {
        return when (chosenLang) {
            "German" -> germanReplacement
            "Italian" -> italianReplacement
            "Czech" -> czechReplacement
            else -> otherReplacement
        }
    }
}

val germanReplacement = mapOf(KeyEvent.VK_SEMICOLON to 1014, //ö
        KeyEvent.VK_EQUALS to KeyEvent.VK_DEAD_GRAVE,
        KeyEvent.VK_SLASH to KeyEvent.VK_MINUS,
        KeyEvent.VK_DEAD_GRAVE to KeyEvent.VK_LESS,
        KeyEvent.VK_OPEN_BRACKET to 1020, //ü
        KeyEvent.VK_BACK_SLASH to KeyEvent.VK_NUMBER_SIGN, //#
        KeyEvent.VK_CLOSE_BRACKET to KeyEvent.VK_PLUS,
        KeyEvent.VK_QUOTE to 996) //ä

val italianReplacement = mapOf(KeyEvent.VK_SEMICOLON to 0x10000f2, //ò
        KeyEvent.VK_EQUALS to 0x10000ec, //ì
        KeyEvent.VK_MINUS to KeyEvent.VK_QUOTE,
        KeyEvent.VK_SLASH to KeyEvent.VK_MINUS,
        KeyEvent.VK_DEAD_GRAVE to KeyEvent.VK_LESS,
        KeyEvent.VK_OPEN_BRACKET to 0x10000e8, //è
        KeyEvent.VK_BACK_SLASH to 0x10000f9, //ù
        KeyEvent.VK_CLOSE_BRACKET to KeyEvent.VK_PLUS,
        KeyEvent.VK_QUOTE to 0x10000e0) //à

val czechReplacement = mapOf(KeyEvent.VK_SEMICOLON to KeyEvent.VK_SEMICOLON, //TODO ů
        KeyEvent.VK_EQUALS to KeyEvent.VK_QUOTE, // '
        KeyEvent.VK_MINUS to KeyEvent.VK_EQUALS,
        KeyEvent.VK_SLASH to KeyEvent.VK_MINUS,
        KeyEvent.VK_DEAD_GRAVE to KeyEvent.VK_SLASH,
        KeyEvent.VK_OPEN_BRACKET to 0x10000fa, //ú
        KeyEvent.VK_BACK_SLASH to KeyEvent.VK_BACK_SLASH, //  ¨
        KeyEvent.VK_CLOSE_BRACKET to KeyEvent.VK_CLOSE_BRACKET, // )
        KeyEvent.VK_QUOTE to KeyEvent.VK_QUOTE) // §

val otherReplacement = mapOf(KeyEvent.VK_SEMICOLON to KeyEvent.VK_SEMICOLON,
        KeyEvent.VK_EQUALS to KeyEvent.VK_EQUALS,
        KeyEvent.VK_COMMA to KeyEvent.VK_COMMA,
        KeyEvent.VK_MINUS to KeyEvent.VK_MINUS,
        KeyEvent.VK_PERIOD to KeyEvent.VK_PERIOD,
        KeyEvent.VK_SLASH to KeyEvent.VK_SLASH,
        KeyEvent.VK_DEAD_GRAVE to KeyEvent.VK_DEAD_GRAVE,
        KeyEvent.VK_OPEN_BRACKET to KeyEvent.VK_OPEN_BRACKET,
        KeyEvent.VK_BACK_SLASH to KeyEvent.VK_BACK_SLASH,
        KeyEvent.VK_CLOSE_BRACKET to KeyEvent.VK_CLOSE_BRACKET,
        KeyEvent.VK_QUOTE to KeyEvent.VK_QUOTE)