package com.dekonoplyov

class Predefined {
    var chosenLang = "German"

    val values = mapOf(
            "German" to GERMAN,
            "Italian" to ITALIAN,
            "Czech" to CZECH,
            "Other" to OTHER,
            "Empty" to ""
    )

    fun getReplacements(): String {
        val some = values[chosenLang]
        return some ?: OTHER
    }
}

private const val GERMAN = """; with ö
= with ´
/ with -
` with <
[ with ü
\ with #
] with +
' with ä
- with ß
"""

private const val ITALIAN = """; with ò
= with ì
- with '
/ with -
` with <
[ with è
\ with ù
] with +
' with à
"""

private const val CZECH = """; with ů
= with '
- with =
/ with -
` with \
[ with ú
\ with ¨
] with )
' with §
1 with +
2 with ě
3 with š
4 with č
5 with ř
6 with ž
7 with ý
8 with á
9 with í
0 with é
"""

private const val OTHER = """; with ;
= with =
- with -
/ with /
` with `
[ with [
\ with \
] with ]
' with '
"""
