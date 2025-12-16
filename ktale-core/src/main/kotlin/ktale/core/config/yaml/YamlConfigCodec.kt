package ktale.core.config.yaml

import com.charleskorn.kaml.Yaml
import ktale.api.config.ConfigCodec
import kotlinx.serialization.KSerializer

/**
 * YAML codec backed by kotlinx.serialization + Kaml.
 *
 * ## Java compatibility note
 * Java plugins can still use typed configs by providing their own [ConfigCodec] implementation
 * (e.g. Jackson). This codec is provided as a batteries-included default for Kotlin users.
 */
public class YamlConfigCodec<T : Any>(
    private val serializer: KSerializer<T>,
    private val yaml: Yaml = DefaultYaml.instance,
) : ConfigCodec<T> {
    override fun decode(text: String): T = yaml.decodeFromString(serializer, text)
    override fun encode(value: T): String = yaml.encodeToString(serializer, value)
}


