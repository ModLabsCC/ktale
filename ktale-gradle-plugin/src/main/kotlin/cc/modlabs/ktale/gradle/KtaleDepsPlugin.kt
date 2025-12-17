package cc.modlabs.ktale.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import java.net.URI

/**
 * Gradle plugin that emits `.dependencies` and optional `.repositories` resources into the jar.
 *
 * ## Purpose
 * When KTale is used as a standalone/bundled server runtime, it can:
 * - load a plugin jar
 * - read `.dependencies` / `.repositories`
 * - download those dependencies into a cache
 * - build a plugin classloader without shading
 *
 * ## Design note
 * This is based on the same idea as your `KPaperGradle` plugin, but intentionally does not
 * generate any server-specific bootstrap classes.
 *
 * See: [KPaperGradle reference implementation](https://raw.githubusercontent.com/ModLabsCC/KPaperGradle/main/src/main/kotlin/cc/modlabs/kpapergradle/KPaperGradlePlugin.kt)
 */
public class KtaleDepsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("ktaleDeps", KtaleDepsExtension::class.java, project.objects)

        // Default mirror for resolving runtime dependencies
        project.repositories.maven { it.url = URI.create("https://nexus.modlabs.cc/repository/maven-mirrors/") }

        val generateTask = project.tasks.register("generateKtaleDependencyManifest") { t ->
            t.group = "build"
            t.description = "Generates .dependencies and optional .repositories into resources for runtime resolution"
            t.inputs.property("deliver", ext.deliverDependencies)
            t.inputs.property("repos", ext.customRepositories)
            t.inputs.property("pluginId", ext.pluginId.orNull)
            t.inputs.property("mainClass", ext.mainClass.orNull)

            t.doLast {
                val buildDir = project.layout.buildDirectory.asFile.get()
                val genResDir = File(buildDir, "generated-resources/ktale-deps")
                genResDir.mkdirs()

                val coords = mutableSetOf<String>()

                // Prefer runtimeClasspath when present (typical for JVM artifacts)
                val config = project.configurations.findByName("runtimeClasspath")
                    ?: project.configurations.findByName("compileClasspath")

                if (config != null && config.isCanBeResolved) {
                    config.resolvedConfiguration.firstLevelModuleDependencies.forEach { dep ->
                        val g = dep.moduleGroup
                        val n = dep.moduleName
                        val v = dep.moduleVersion
                        if (!g.isNullOrBlank() && !n.isNullOrBlank() && !v.isNullOrBlank()) {
                            coords += "$g:$n:$v"
                        }
                    }
                }

                coords += ext.deliverDependencies

                File(genResDir, ".dependencies").writeText(coords.sorted().joinToString("\n") + "\n")

                val reposFile = File(genResDir, ".repositories")
                if (ext.customRepositories.isNotEmpty()) {
                    val lines = ext.customRepositories.map { r ->
                        buildString {
                            append(r.id).append(' ').append(r.url)
                            if (!r.usernameEnv.isNullOrBlank() && !r.passwordEnv.isNullOrBlank()) {
                                append(' ').append(r.usernameEnv).append(' ').append(r.passwordEnv)
                            }
                        }
                    }
                    reposFile.writeText(lines.joinToString("\n") + "\n")
                } else {
                    if (reposFile.exists()) reposFile.delete()
                }

                // Optional: generate ktale-plugin.properties for standalone hosts
                val pid = ext.pluginId.orNull?.trim().orEmpty()
                val main = ext.mainClass.orNull?.trim().orEmpty()
                val descriptor = File(genResDir, "ktale-plugin.properties")
                if (pid.isNotBlank() && main.isNotBlank()) {
                    descriptor.writeText("id=$pid\nmain=$main\n")
                } else {
                    if (descriptor.exists()) descriptor.delete()
                }
            }
        }

        // Copy into the main resources directory so it ends up inside the jar.
        project.tasks.matching { it.name == "processResources" }.configureEach { task ->
            task.dependsOn(generateTask)
            task.doLast {
                val buildDir = project.layout.buildDirectory.asFile.get()
                val genResDir = File(buildDir, "generated-resources/ktale-deps")
                val resourcesDir = File(buildDir, "resources/main")
                resourcesDir.mkdirs()

                val deps = File(genResDir, ".dependencies")
                if (deps.exists()) deps.copyTo(File(resourcesDir, ".dependencies"), overwrite = true)

                val repos = File(genResDir, ".repositories")
                if (repos.exists()) repos.copyTo(File(resourcesDir, ".repositories"), overwrite = true)

                val desc = File(genResDir, "ktale-plugin.properties")
                if (desc.exists()) desc.copyTo(File(resourcesDir, "ktale-plugin.properties"), overwrite = true)
            }
        }
    }
}

public open class KtaleDepsExtension(objects: ObjectFactory) {
    /**
     * Extra dependencies that should be emitted even if they are not first-level runtime deps.
     *
     * Example: `deliver("com.foo:bar:1.2.3")`
     */
    public val deliverDependencies: MutableList<String> = mutableListOf()

    internal val customRepositories: MutableList<RepoSpec> = mutableListOf()

    /** Optional: plugin id to write into `ktale-plugin.properties`. */
    public val pluginId: Property<String> = objects.property(String::class.java)

    /** Optional: main class to write into `ktale-plugin.properties`. */
    public val mainClass: Property<String> = objects.property(String::class.java)

    public fun deliver(vararg deps: String) {
        deliverDependencies += deps
    }

    /** DSL: repository("https://repo1.maven.org/maven2/") */
    public fun repository(url: String) {
        val host = try { URI(url).host ?: url } catch (_: Exception) { url }
        val id = host.replace(Regex("[^a-zA-Z0-9-_]"), "-")
        customRepositories += RepoSpec(id, url)
    }

    /** DSL: repository("myRepo", "https://repo.example.com/maven/") */
    public fun repository(id: String, url: String) {
        customRepositories += RepoSpec(id, url)
    }

    /**
     * DSL: repositoryWithAuth("private", "https://repo.example.com/maven/", "REPO_USER", "REPO_PASS")
     *
     * IMPORTANT: only the environment variable names are written into the jar.
     */
    public fun repositoryWithAuth(id: String, url: String, userEnvVar: String, passEnvVar: String) {
        customRepositories += RepoSpec(id, url, userEnvVar, passEnvVar)
    }

    internal data class RepoSpec(
        val id: String,
        val url: String,
        val usernameEnv: String? = null,
        val passwordEnv: String? = null,
    )
}


