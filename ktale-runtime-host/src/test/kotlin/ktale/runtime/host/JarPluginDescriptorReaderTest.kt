package ktale.runtime.host

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class JarPluginDescriptorReaderTest : FunSpec({
    test("reads ktale-plugin.properties from jar") {
        val tmp = Files.createTempFile("ktale-plugin", ".jar")
        JarOutputStream(Files.newOutputStream(tmp)).use { jar ->
            jar.putNextEntry(JarEntry("ktale-plugin.properties"))
            jar.write("id=test\nmain=example.Main\n".toByteArray(Charsets.UTF_8))
            jar.closeEntry()
        }

        val desc = JarPluginDescriptorReader.read(tmp)
        desc.id shouldBe "test"
        desc.mainClass shouldBe "example.Main"
    }
})


