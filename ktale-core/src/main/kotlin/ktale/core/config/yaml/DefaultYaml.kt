package ktale.core.config.yaml

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration

/**
 * Default YAML configuration used by KTale core.
 *
 * ## Design note
 * This is intentionally conservative. Plugins that need different YAML behavior should supply their own [Yaml].
 */
public object DefaultYaml {
    public val instance: Yaml = Yaml(
        configuration = YamlConfiguration(
            encodeDefaults = true,
            strictMode = false,
        )
    )
}


