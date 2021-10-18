package net.kodein.demo.crypto


public fun ByteArray.toHex(): String =
    joinToString("") { it.toUByte().toString(radix = 16).padStart(2, '0') }