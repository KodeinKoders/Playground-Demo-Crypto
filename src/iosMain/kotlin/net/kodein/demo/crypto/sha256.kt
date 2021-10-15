package net.kodein.demo.crypto

import kotlinx.cinterop.*
import platform.CoreCrypto.*


private class Sha256Ios() : Digest {

    private companion object {
        fun newContext(): CC_SHA256_CTX = nativeHeap.alloc<CC_SHA256_CTX>().also { CC_SHA256_Init(it.ptr) }
    }

    private var ctx: CC_SHA256_CTX? = newContext()

    override fun getDigestSize(): Int = CC_SHA256_DIGEST_LENGTH

    override fun update(input: Byte) {
        update(byteArrayOf(input), 0, 1)
    }

    override fun update(input: ByteArray, inputOffset: Int, len: Int) {
        if (len == 0) return
        require(inputOffset >= 0 && len >= 0)
        require(inputOffset + len <= input.size) { "inputOffset + len > input.size" }

        val c = ctx ?: error("Digest closed (doFinal has been called). Call reset to restart a new one.")

        input.usePinned {
            CC_SHA256_Update(c.ptr, it.addressOf(inputOffset), len.toUInt())
        }
    }

    override fun doFinal(out: ByteArray, outOffset: Int) {
        require(outOffset >= 0)
        require(out.size - outOffset >= CC_SHA256_DIGEST_LENGTH) { "Output array is too small (need $CC_SHA256_DIGEST_LENGTH bytes)" }

        val c = ctx ?: error("Digest closed (doFinal has been called). Call reset to restart a new one.")

        out.asUByteArray().usePinned {
            CC_SHA256_Final(it.addressOf(outOffset), c.ptr)
        }
        nativeHeap.free(c)
        ctx = null
    }

    override fun reset() {
        ctx?.let { nativeHeap.free(it) }
        ctx = newContext()
    }

}

public actual fun sha256(): Digest = Sha256Ios()
