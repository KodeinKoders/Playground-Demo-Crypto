package net.kodein.demo.crypto

public interface Secp256k1 {

    public fun verifySecKey(secKey: ByteArray): Boolean

    public fun createPubKey(secKey: ByteArray): ByteArray

    public fun signMessage(message: ByteArray, secKey: ByteArray): ByteArray

    public fun verifySignature(signature: ByteArray, message: ByteArray, pubKey: ByteArray): Boolean

}

public expect fun secp256k1(): Secp256k1
