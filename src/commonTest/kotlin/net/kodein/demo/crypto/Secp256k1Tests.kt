package net.kodein.demo.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class Secp256k1Tests {
    @Test
    fun verify() {
        val secp256k1 = secp256k1()
        val secKey = ByteArray(32) { it.toByte() }
        assertTrue(secp256k1.verifySecKey(secKey))
        val pubKey = secp256k1.createPubKey(secKey)
        assertNotNull(pubKey)
        assertEquals("046d6caac248af96f6afa7f904f550253a0f3ef3f5aa2fe6838a95b216691468e2487e6222a6664e079c8edf7518defd562dbeda1e7593dfd7f0be285880a24dab", pubKey.toHex())
        val message = "Hello, World!".encodeToByteArray()
        val signature = secp256k1.signMessage(message, secKey)
        assertTrue(secp256k1.verifySignature(signature, message, pubKey))
    }
}