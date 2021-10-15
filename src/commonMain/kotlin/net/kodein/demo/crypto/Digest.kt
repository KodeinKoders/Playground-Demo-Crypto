package net.kodein.demo.crypto


public interface Digest {
    public fun getDigestSize(): Int

    public fun update(input: Byte)

    public fun update(input: ByteArray, inputOffset: Int, len: Int)

    public fun doFinal(out: ByteArray, outOffset: Int)

    public fun reset()
}

public fun Digest.hash(input: ByteArray, inputOffset: Int, len: Int): ByteArray {
    reset()
    update(input, inputOffset, len)
    val output = ByteArray(getDigestSize())
    doFinal(output, 0)
    return output
}

public fun Digest.hash(input: ByteArray): ByteArray = hash(input, 0, input.size)

public expect fun sha256(): Digest
