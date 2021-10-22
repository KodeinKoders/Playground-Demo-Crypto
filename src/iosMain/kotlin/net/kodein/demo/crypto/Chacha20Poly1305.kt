package net.kodein.demo.crypto

import chachaPoly.DataResult
import chachaPoly.SwiftChachaPoly
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.pin
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy


private inline fun ByteArray.toData(): NSData {
    if (isEmpty()) return NSData()
    val pinned = pin()
    return NSData.create(
        bytesNoCopy = pinned.addressOf(0),
        length = size.toULong(),
        deallocator = { _, _ -> pinned.unpin() }
    )
}

private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return bytes
}

private fun DataResult.unwrap(): NSData {
    this.failure()?.let { error(it.description ?: "Unknown error") }
    return success() ?: error("Invalid result")
}

private class Chacha20Poly1305Ios : Chacha20Poly1305 {
    override fun encrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, plainText: ByteArray): ByteArray {
        require(key.size == 32) { "Key must be 32 bytes" }
        require(nonce.size == 12) { "Nonce must be 12 bytes" }
        autoreleasepool {
            return SwiftChachaPoly.encryptWithKey(
                key.toData(),
                nonce.toData(),
                aad.toData(),
                plainText.toData()
            ).unwrap().toByteArray()
        }
    }

    override fun decrypt(key: ByteArray, nonce: ByteArray, aad: ByteArray, cipherText: ByteArray): ByteArray {
        require(key.size == 32) { "Key must be 32 bytes" }
        require(nonce.size == 12) { "Nonce must be 12 bytes" }
        autoreleasepool {
            return SwiftChachaPoly.decryptWithKey(
                key.toData(),
                nonce.toData(),
                aad.toData(),
                cipherText.toData()
            ).unwrap().toByteArray()
        }
    }

}

public actual fun chacha20Poly1305(): Chacha20Poly1305 = Chacha20Poly1305Ios()

