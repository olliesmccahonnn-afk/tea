# Myth Client (full build project)

One Fabric project that builds a single jar containing the base Myth Client (0.1.0) plus all 43 add-on modules.

## Build
1. Install JDK 21 and Gradle (9.x).
2. Open `gradle.properties` and check `yarn_mappings` and `fabric_version` against https://fabricmc.net/develop (Minecraft 1.21.11).
3. In this folder: `gradle wrapper` once, then `./gradlew build` (Windows: `gradlew.bat build`).
4. Install `build/libs/myth-client-1.0.0.jar` (ignore `-sources`) into `.minecraft/mods/` with Fabric Loader + Fabric API.
   Remove any older Myth Client jar first.

## How it fits together
- The base client has no source in this project; it is the prebuilt jar in `libs/maven/...`. Loom remaps it so the add-ons compile against it, then nests it inside the output jar.
- `dev/mythclient/addons/MythAddons.java` registers every add-on into the base `ModuleManager` at startup (this replaces editing `registerDefaults()`).
- Add-on mixins use their own config, `mythclient-addons.mixins.json`. New lang keys are in `assets/mythclient/lang/en_us.json`.
- If nesting fails, delete the `include` line in `build.gradle` and put the base jar from `libs/maven/.../myth-client-base-0.1.0.jar` into `mods/` next to the add-on jar.

## Other
- `extras/standalone-cosmetics/` is the separate cosmetics-only mod (own Gradle project). Not part of this build.
- `docs/ORIGINAL-INTEGRATION-NOTES.md` has the per-batch troubleshooting hints (API names to adjust if compile fails).
- Never compiled here (no network/toolchain). Expect to fix a few 1.21.11 API names on first build.
