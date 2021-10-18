package net.kodein.demo.crypto

import kotlin.test.Test
import kotlin.test.assertEquals


class Sha256Tests {
    @Test fun digest() {
        assertEquals("dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f", sha256().hash("Hello, World!".encodeToByteArray()).toHex())
    }
}
