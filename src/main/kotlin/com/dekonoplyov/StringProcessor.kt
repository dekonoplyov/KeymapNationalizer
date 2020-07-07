package com.dekonoplyov

class StringProcessor {
    companion object {
        private val regex = Regex("\\s+")
        fun removeDuplicateSpaces(s: String) = regex.replace(s, " ")
        fun process(s: String) = removeDuplicateSpaces(s.trim().toLowerCase())
    }
}