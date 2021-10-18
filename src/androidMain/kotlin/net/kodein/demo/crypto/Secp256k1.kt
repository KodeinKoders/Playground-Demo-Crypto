package net.kodein.demo.crypto

private class Secp256k1Android : Secp256k1 {

    private inline fun <T> useContext(type: Secp256k1Jni.ContextType, block: (Long) -> T): T {
        val ctx = Secp256k1Jni.createContext(type).takeIf { it != 0L } ?: error("Could not create a secp256k1 context")
        try {
            return block(ctx)
        } finally {
            Secp256k1Jni.destroyContext(ctx)
        }
    }

    override fun verifySecKey(secKey: ByteArray): Boolean {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(Secp256k1Jni.ContextType.sign) {
            return Secp256k1Jni.verifySecKey(it, secKey)
        }
    }

    override fun createPubKey(secKey: ByteArray): ByteArray {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(Secp256k1Jni.ContextType.sign) {
            return Secp256k1Jni.createPubKey(it, secKey) ?: error("Operation failed")
        }
    }

    override fun signMessage(message: ByteArray, secKey: ByteArray): ByteArray {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(Secp256k1Jni.ContextType.sign) {
            return Secp256k1Jni.signMessage(it, message, secKey) ?: error("Operation failed")
        }
    }

    override fun verifySignature(signature: ByteArray, message: ByteArray, pubKey: ByteArray): Boolean {
        require(signature.size == 64) { "Signature must be 64 bytes" }
        require(pubKey.size == 65) { "Public key must be 65 bytes" }
        useContext(Secp256k1Jni.ContextType.verify) {
            val ret = Secp256k1Jni.verifySignature(it, signature, message, pubKey)
            return when (ret) {
                -1 -> error("Operation failed")
                0 -> false
                1 -> true
                else -> error("Unknown error")
            }
        }
    }
}

private var isInitialized = false

public actual fun secp256k1(): Secp256k1 {
    if (!isInitialized) {
        System.loadLibrary("secp256k1-jni")
        isInitialized = true
    }
    return Secp256k1Android()
}
