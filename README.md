# Myth Client (base)

A small, clean Fabric client base for Minecraft 1.21.11 (Java 21). You get the framework; add features as modules.

## What's in it
| Part | Where | What it does |
|---|---|---|
| Entry point | `MythClient` | wires modules, config, keybinds, tick + HUD hooks |
| Module system | `core/ClientModule`, `core/ModuleManager` | on/off, per-tick + per-frame hooks, one keybind per module |
| Settings | `setting/*` | on/off, number (slider) and pick-one settings - saved automatically |
| Config | `core/ConfigManager` | `config/mythclient.json` |
| Click GUI | `gui/ClickGuiScreen` | grey + white. **Right Shift** opens it |
| HUD editor | `gui/HudEditorScreen` | drag any HUD element (Coordinates, FPS, ...) where you want it |
| Waypoints | `module/world/*`, `gui/WaypointScreen` | add / delete / hide waypoints; optional HUD list with distance + arrow |
| Modules | `module/*` | Watermark, FPS, Coordinates, Waypoints (HUD) - Toggle Sprint, Toggle Sneak (movement) |

## Using it
* **Right Shift** - menu. Left click a module = on/off (white row = on). Right click = its settings (click to toggle/cycle, drag a bar for numbers).
* **HUD Editor** (button at the bottom of the menu) - drag Coordinates (or any other HUD element that is switched on). It can't be dragged off the screen; positions save when you let go.
* **Waypoints** (button at the bottom of the menu) - type a name, press *Add here*. Press **B** anywhere in a world to drop one instantly (rebind in Controls). Turn on the *Waypoints* module to see the nearest ones on your HUD.
* **Toggle Sneak** - tap sneak to keep sneaking, tap again to stop (uses Minecraft's own toggle-sneak option while the module is on, and restores your old setting when you turn it off).
* **Nothing on screen by default:** every module starts OFF, so a fresh install draws nothing. Optional key *Hide / show Myth HUD* (Controls > Myth Client, unbound) hides all Myth HUD text instantly.
* Module keybinds (all unbound) are in **Options > Controls > Myth Client**.

## Build
1. Easiest way to get the Gradle wrapper + exact versions: https://fabricmc.net/develop - pick **Minecraft 1.21.11**, **Yarn** mappings, Java 21. Download it.
2. Copy `gradlew`, `gradlew.bat` and the `gradle/` folder from that download into this project.
3. In `gradle.properties` set `yarn_mappings` and `fabric_version` to what the generator shows (the two marked "CHECK").
4. `./gradlew build` (Windows: `gradlew build`). Jar: `build/libs/` (the one **without** `-sources`). Quick test: `./gradlew runClient`.

## Add a module
```java
public final class ExampleModule extends ClientModule {
    private final BoolSetting loud = setting(new BoolSetting("Loud", false));
    public ExampleModule() { super("Example", "Does a thing.", Category.MISC); }
    @Override protected void onEnable()  { }
    @Override protected void onDisable() { }
    @Override public void onTick()       { }   // every tick while enabled + in a world
}
```
Register it in `ModuleManager.registerDefaults()` and add `"key.mythclient.module.example": "Toggle Example"` to `assets/mythclient/lang/en_us.json`.
For HUD text extend `HudModule` and return the lines from `lines()` - it gets dragging + clamping for free.

## Notes
* Written against Yarn names for 1.21.11; **not compiled against the real game by me** (no Minecraft libraries offline). If the build prints errors, send them to me.
* Mouse input is read straight from GLFW (see `gui/MouseInput`) instead of `mouseClicked`/`mouseDragged`, because Minecraft changed those classes in 1.21.9.
* Waypoints are shown on the HUD (distance + arrow), not as in-world beams - 3D world rendering changed a lot in 1.21.x and is a separate, bigger job.
