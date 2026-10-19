package com.example

import com.example.core.model.ProtocolType
import com.example.core.parser.UriParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testVlessParsing() {
        val uri = "vless://4a390971-8608-4e89-a212-e87a220d9319@fra.lonak.cloud:443?security=reality&sni=speed.cloudflare.com&fp=chrome&pbk=Gz6Qk2UuO0MhB6X1B8tK7Yy1Z2W3V4U5T6S7R8Q9P0O&sid=1a2b3c4d&type=tcp&flow=xtls-rprx-vision#Germany+Reality"
        val server = UriParser.parseLine(uri)
        assertNotNull(server)
        assertEquals(ProtocolType.VLESS, server?.protocol)
        assertEquals("fra.lonak.cloud", server?.host)
        assertEquals(443, server?.port)
        assertEquals("DE", server?.countryCode)
    }

    @Test
    fun testTrojanParsing() {
        val uri = "trojan://lonakPass998@ams.lonak.cloud:443?security=tls&sni=ams.speedtest.net#Netherlands+Stable"
        val server = UriParser.parseLine(uri)
        assertNotNull(server)
        assertEquals(ProtocolType.TROJAN, server?.protocol)
        assertEquals("ams.lonak.cloud", server?.host)
        assertEquals(443, server?.port)
        assertEquals("NL", server?.countryCode)
    }

    @Test
    fun testHysteria2Parsing() {
        val uri = "hysteria2://mytoken@hel.lonak.cloud:8443?sni=www.bing.com#Finland+Fast"
        val server = UriParser.parseLine(uri)
        assertNotNull(server)
        assertEquals(ProtocolType.HYSTERIA2, server?.protocol)
        assertEquals("hel.lonak.cloud", server?.host)
        assertEquals(8443, server?.port)
        assertEquals("FI", server?.countryCode)
    }

    @Test
    fun testCountryExtraction() {
        val (cc1, cn1) = UriParser.extractCountry("🇩🇪 Frankfurt Server", "1.1.1.1")
        assertEquals("DE", cc1)
        assertEquals("Germany", cn1)

        val (cc2, cn2) = UriParser.extractCountry("Istanbul Fast", "ist.lonak.cloud")
        assertEquals("TR", cc2)
        assertEquals("Turkey", cn2)
    }

    @Test
    fun testSubscriptionDeduplication() {
        val raw = """
            vless://uuid@srv1.lonak.cloud:443#srv1
            vless://uuid@srv1.lonak.cloud:443#srv1_duplicate
            trojan://pass@srv2.lonak.cloud:443#srv2
        """.trimIndent()

        val parsed = UriParser.parseSubscriptionContent(raw)
        assertEquals(2, parsed.size)
    }
}
