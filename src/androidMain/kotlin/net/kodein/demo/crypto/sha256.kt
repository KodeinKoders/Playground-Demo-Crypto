package net.kodein.demo.crypto

import java.security.MessageDigest


private class Sha256Android() : Digest {

    private val digest = MessageDigest.getInstance("SHA-256")

    override fun getDigestSize(): Int = digest.digestLength

    override fun update(input: Byte) { digest.update(input) }

    override fun update(input: ByteArray, inputOffset: Int, len: Int) { digest.update(input, inputOffset, len) }

    override fun doFinal(out: ByteArray, outOffset: Int) { digest.digest(out, outOffset, out.size - outOffset) }

    override fun reset() { digest.reset() }

}

public actual fun sha256(): Digest = Sha256Android()
