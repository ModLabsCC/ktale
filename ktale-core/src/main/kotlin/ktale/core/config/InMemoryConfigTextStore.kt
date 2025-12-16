package ktale.core.config

import java.util.concurrent.ConcurrentHashMap

/**
 * Simple in-memory config storage used for tests and fake platforms.
 */
public class InMemoryConfigTextStore : ConfigTextStore {
    private val map = ConcurrentHashMap<String, String>()

    override fun read(id: String): String? = map[id]

    override fun write(id: String, text: String) {
        map[id] = text
    }
}


