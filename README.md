# KTale

KTale is a lightweight, Kotlin-first helper library for building **Hytale Server plugins**. It focuses on small, composable utilities and extension functions that stay close to the underlying Hytale API while cutting boilerplate.

## Documentation

- **API reference (Dokka)**: https://modlabscc.github.io/ktale/
- **Guides (in this repo)**:
  - [`docs/index.mdx`](docs/index.mdx)
  - [`docs/message-formatting.mdx`](docs/message-formatting.mdx)
  - [`docs/custom-guis.mdx`](docs/custom-guis.mdx)

## Installation (Gradle)

KTale is published as a Maven artifact:

- **Group**: `cc.modlabs`
- **Artifact**: `ktale`
- **Version**: calendar-based (example: `2026.1.15.2109`)

```kotlin
repositories {
  maven("https://nexus.modlabs.cc/repository/maven-mirrors/")
}

dependencies {
  implementation("cc.modlabs:ktale:<version>")
}
```

> Note: Hytale’s server API is expected to be provided by the server runtime (your plugin compiles against it, but you typically don’t ship it).

## What’s inside

KTale is organized into small packages (see the API reference for full details):

- **`cc.modlabs.ktale.ext`**: Kotlin extensions for common plugin tasks (messaging, titles, notifications, permissions, teleport helpers, etc.).
- **`cc.modlabs.ktale.text`**: A small MiniMessage-like layer for building Hytale `Message` objects (`MessageBuilder`).
- **`cc.modlabs.ktale.ui`**: Helpers for Hytale custom UI:
  - `UiPath` path helpers (e.g. `"#Node.Value"`)
  - `UICommandBuilder` / `UIEventBuilder` convenience extensions
  - `CustomUIPage<T>` base class + `ui { ... }` build scope
- **`cc.modlabs.ktale.entitystats`**: Entity stat helpers (`EntityStats`, `Player.entityStatMapOrNull()`, `EntityStatMap.metaSnapshot()`, etc.).
- **`cc.modlabs.ktale.blocks`**: Block utilities (drop computation helpers and small type heuristics like `BlockType.isOre()`).

## Quick examples

### Send formatted chat (MiniMessage-like)

```kotlin
import cc.modlabs.ktale.ext.send

player.send("<red>Hello <bold>World</bold></red>")
player.send("<gradient:red:blue>Gradient text</gradient>")
player.send("<#FFAA00>Hex colors</#FFAA00>")
```

### Build a custom UI page

See the full guide in [`docs/custom-guis.mdx`](docs/custom-guis.mdx). At a high level, KTale helps keep `UICommandBuilder` + `UIEventBuilder` wiring concise and provides `CustomUIPage<T>` update helpers.

## License

GPL-3.0 — see [`LICENSE`](LICENSE).
