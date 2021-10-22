package net.kodein.demo.crypto

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private class Chacha20Poly1305Android : Chacha20Poly1305 {
    companion object {
        const val ALGO_NAME = "ChaCha20-Poly1305"
    }

    override fun encrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, plainText: ByteArray): ByteArray {
        require(key.size == 32) { "Key must be 32 bytes" }
        require(nonce.size == 12) { "Nonce must be 12 bytes" }
        val cipher = Cipher.getInstance(ALGO_NAME)
        val secretKey = SecretKeySpec(key, ALGO_NAME)
        val iv = IvParameterSpec(nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        cipher.updateAAD(aad)
        return cipher.doFinal(plainText)
    }

    override fun decrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, cipherText: ByteArray): ByteArray {
        require(key.size == 32) { "Key must be 32 bytes" }
        require(nonce.size == 12) { "Nonce must be 12 bytes" }
        val cipher = Cipher.getInstance(ALGO_NAME)
        val secretKey = SecretKeySpec(key, ALGO_NAME)
        val iv = IvParameterSpec(nonce)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        cipher.updateAAD(aad)
        return cipher.doFinal(cipherText)
    }
}

public actual fun chacha20Poly1305(): Chacha20Poly1305 = Chacha20Poly1305Android()
