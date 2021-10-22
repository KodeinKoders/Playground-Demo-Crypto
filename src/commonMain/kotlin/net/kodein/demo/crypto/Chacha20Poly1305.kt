package net.kodein.demo.crypto


public interface Chacha20Poly1305 {

    public fun encrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, plainText: ByteArray): ByteArray

    public fun decrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, cipherText: ByteArray): ByteArray
}

public expect fun chacha20Poly1305(): Chacha20Poly1305
