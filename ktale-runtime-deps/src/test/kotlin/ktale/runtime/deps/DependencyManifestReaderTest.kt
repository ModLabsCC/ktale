package ktale.runtime.deps

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.util.Collections

class DependencyManifestReaderTest : FunSpec({
    test("reads dependencies and repositories resources") {
        val loader = MapResourceClassLoader(
            mapOf(
                ".dependencies" to """
                    # comment
                    cc.modlabs:ktale-api:1.0.0
                    
                    org.example:lib:2.0.0
                """.trimIndent(),
                ".repositories" to """
                    # comment
                    modlabs https://nexus.modlabs.cc/repository/maven-mirrors/
                    priv https://repo.example.com/maven/ USER_ENV PASS_ENV
                """.trimIndent(),
            )
        )

        val manifest = DependencyManifestReader.fromResources(loader)
        manifest.coordinates shouldBe listOf("cc.modlabs:ktale-api:1.0.0", "org.example:lib:2.0.0")
        manifest.repositories shouldBe listOf(
            DependencyManifest.Repository("modlabs", "https://nexus.modlabs.cc/repository/maven-mirrors/"),
            DependencyManifest.Repository("priv", "https://repo.example.com/maven/", "USER_ENV", "PASS_ENV"),
        )
    }
})

private class MapResourceClassLoader(
    private val resources: Map<String, String>,
) : ClassLoader(null) {
    override fun getResourceAsStream(name: String): java.io.InputStream? {
        val v = resources[name] ?: return null
        return ByteArrayInputStream(v.toByteArray(Charsets.UTF_8))
    }
}


