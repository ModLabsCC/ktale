package ktale.runtime.deps

import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.util.repository.AuthenticationBuilder
import java.nio.file.Path

/**
 * Resolves Maven coordinates to local jar files using Maven Resolver (Aether).
 *
 * ## Intended usage
 * This is for a standalone KTale host runtime that builds plugin classloaders from resolved jars.
 *
 * ## Non-goal
 * This class does not modify classpaths. It only downloads and returns file paths.
 */
public class MavenDependencyResolver(
    cacheDir: Path,
    repositories: List<RemoteRepository> = listOf(
        RemoteRepository.Builder("modlabs-mirror", "default", "https://nexus.modlabs.cc/repository/maven-mirrors/").build()
    ),
) {
    private val repoSystem: RepositorySystem = newRepoSystem()
    private val session: RepositorySystemSession = newSession(repoSystem, cacheDir)
    private val repos: List<RemoteRepository> = repositories

    /**
     * Resolves all [coordinates] (and their transitive runtime dependencies) to local jar paths.
     *
     * @param coordinates Maven coords in `group:artifact:version`
     */
    public fun resolve(coordinates: List<String>): List<Path> {
        val results = mutableListOf<Path>()
        for (coord in coordinates) {
            val rootDep = Dependency(DefaultArtifact(coord), "runtime")
            val collect = CollectRequest().apply {
                root = rootDep
                repositories = repos
            }
            val request = DependencyRequest(collect, null)
            val resolved = repoSystem.resolveDependencies(session, request)
            for (artifactResult in resolved.artifactResults) {
                val file = artifactResult.artifact.file ?: continue
                results.add(file.toPath())
            }
        }
        return results.distinct()
    }

    public companion object {
        /**
         * Builds a [RemoteRepository], optionally attaching authentication from environment variables.
         *
         * @param usernameEnv env var name for username, if any
         * @param passwordEnv env var name for password/token, if any
         */
        public fun repo(
            id: String,
            url: String,
            usernameEnv: String? = null,
            passwordEnv: String? = null,
        ): RemoteRepository {
            val builder = RemoteRepository.Builder(id, "default", url)
            val user = usernameEnv?.let { System.getenv(it) }?.takeIf { it.isNotBlank() }
            val pass = passwordEnv?.let { System.getenv(it) }?.takeIf { it.isNotBlank() }
            if (user != null && pass != null) {
                builder.setAuthentication(AuthenticationBuilder().addUsername(user).addPassword(pass).build())
            }
            return builder.build()
        }

        private fun newRepoSystem(): RepositorySystem {
            val locator = DefaultServiceLocator()
            locator.addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
            locator.addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
            return locator.getService(RepositorySystem::class.java)
        }

        private fun newSession(system: RepositorySystem, cacheDir: Path): DefaultRepositorySystemSession {
            val session = DefaultRepositorySystemSession()
            session.localRepositoryManager = system.newLocalRepositoryManager(session, LocalRepository(cacheDir.toFile()))
            return session
        }
    }
}


