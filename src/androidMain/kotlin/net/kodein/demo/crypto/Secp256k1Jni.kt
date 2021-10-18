package net.kodein.demo.crypto


internal object Secp256k1Jni {
    @JvmInline value class ContextType private constructor(val type: Int) {
        companion object {
            val sign = ContextType(0)
            val verify = ContextType(1)
        }
    }

    external fun createContext(type: ContextType): Long
    external fun destroyContext(ctx: Long)

    external fun verifySecKey(ctx: Long, seckey: ByteArray?): Boolean
    external fun createPubKey(ctx: Long, seckey: ByteArray?): ByteArray?
    external fun signMessage(ctx: Long, msg: ByteArray?, seckey: ByteArray?): ByteArray?
    external fun verifySignature(ctx: Long, sig: ByteArray?, msg: ByteArray?, pubkey: ByteArray?): Int
}
