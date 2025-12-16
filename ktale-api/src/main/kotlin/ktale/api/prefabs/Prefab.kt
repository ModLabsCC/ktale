package ktale.api.prefabs

import ktale.api.identity.Key

/**
 * Opaque prefab payload.
 *
 * ## Design note
 * KTale does not assume the prefab format (binary/JSON/custom).
 * The payload is treated as opaque bytes plus a best-effort [formatHint].
 */
public interface Prefab {
    public val id: Key
    public val formatHint: String?
    public val bytes: ByteArray
}


