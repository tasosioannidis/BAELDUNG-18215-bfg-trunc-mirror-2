package com.baeldung.inputstream

import java.io.InputStream

fun InputStream.readUpToNullChar(nullChar: Char = '\n'): String {
    val stringBuilder = StringBuilder()
    var currentChar = this.read().toChar()
    while (currentChar != nullChar) {
        stringBuilder.append(currentChar)
        currentChar = this.read().toChar()
        if (this.available() <= 0) {
            stringBuilder.append(currentChar)
            break
        }
    }
    return stringBuilder.toString()
}