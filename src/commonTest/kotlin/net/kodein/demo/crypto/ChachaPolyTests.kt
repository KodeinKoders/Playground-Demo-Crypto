package net.kodein.demo.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ChachaPolyTests {
    @Test
    fun encryptAndDecrypt() {
        val chachaPoly = chacha20Poly1305()
        val secKey = ByteArray(32) { it.toByte() }
        val nonce = ByteArray(12) { it.toByte() }
        val aad = "Additional authenticated data".encodeToByteArray()
        val plainText = "Plain text".encodeToByteArray()
        val cipherText = chachaPoly.encrypt(secKey, nonce, aad, plainText)
        assertEquals("d99769694737d125cff75d8cc60242ac26404bbfed659b175573", cipherText.toHex())
        val decipheredPlainText = chachaPoly.decrypt(secKey, nonce, aad, cipherText)
        assertTrue(plainText.contentEquals(decipheredPlainText))
    }
}