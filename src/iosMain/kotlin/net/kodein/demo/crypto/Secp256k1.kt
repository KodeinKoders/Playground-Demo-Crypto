package net.kodein.demo.crypto

import kotlinx.cinterop.*
import platform.posix.size_tVar
import secp256k1.*

private class Secp256k1Ios : Secp256k1 {

    private inline fun <T> useContext(flag: Int, block: (CPointer<secp256k1_context>) -> T): T {
        val ctx = secp256k1_context_create(flag.toUInt()) ?: error("Could not create a secp256k1 context")

        try {
            return block(ctx)
        } finally {
            secp256k1_context_destroy(ctx)
        }
    }
    
    private fun Int.check() {
        if (this == 0) error("Operation failed")
    }

    override fun verifySecKey(secKey: ByteArray): Boolean {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(SECP256K1_CONTEXT_SIGN) { ctx ->
            return secp256k1_ec_seckey_verify(ctx, secKey.asUByteArray().refTo(0)) != 0
        }
    }

    override fun createPubKey(secKey: ByteArray): ByteArray {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(SECP256K1_CONTEXT_SIGN) { ctx ->
            memScoped {
                val pk = alloc<secp256k1_pubkey>()
                secp256k1_ec_pubkey_create(ctx, pk.ptr, secKey.asUByteArray().refTo(0)).check()
                val pubKey = ByteArray(65)
                val len = alloc<size_tVar>().also { it.value = 65.convert() }
                secp256k1_ec_pubkey_serialize(ctx, pubKey.asUByteArray().refTo(0), len.ptr, pk.ptr, SECP256K1_EC_UNCOMPRESSED)
                return pubKey
            }
        }
    }

    override fun signMessage(message: ByteArray, secKey: ByteArray): ByteArray {
        require(secKey.size == 32) { "secret key must be 32 bytes" }
        useContext(SECP256K1_CONTEXT_SIGN) { ctx ->
            memScoped {
                val sig = alloc<secp256k1_ecdsa_signature>()
                secp256k1_ecdsa_sign(ctx, sig.ptr, message.asUByteArray().refTo(0), secKey.asUByteArray().refTo(0), null, null).check()
                val signature = ByteArray(64)
                secp256k1_ecdsa_signature_serialize_compact(ctx, signature.asUByteArray().refTo(0), sig.ptr).check()
                return signature
            }
        }
    }

    override fun verifySignature(signature: ByteArray, message: ByteArray, pubKey: ByteArray): Boolean {
        require(signature.size == 64) { "Signature must be 64 bytes" }
        require(pubKey.size == 65) { "Public key must be 65 bytes" }
        useContext(SECP256K1_CONTEXT_VERIFY) { ctx ->
            memScoped {
                val sig = alloc<secp256k1_ecdsa_signature>()
                secp256k1_ecdsa_signature_parse_compact(ctx, sig.ptr, signature.asUByteArray().refTo(0)).check()
                val pk = alloc<secp256k1_pubkey>()
                secp256k1_ec_pubkey_parse(ctx, pk.ptr, pubKey.asUByteArray().refTo(0), pubKey.size.convert()).check()
                val ret = secp256k1_ecdsa_verify(ctx, sig.ptr, message.asUByteArray().refTo(0), pk.ptr)
                return ret != 0
            }
        }
    }
}

public actual fun secp256k1(): Secp256k1 = Secp256k1Ios()
