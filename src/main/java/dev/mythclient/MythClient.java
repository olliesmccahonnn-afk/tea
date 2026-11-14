package dev.mythclient;

import dev.mythclient.core.ConfigManager;
import dev.mythclient.core.ModuleManager;
import dev.mythclient.gui.ClickGuiScreen;
import dev.mythclient.module.world.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Myth Client - entry point.
 *
 * Wires everything together: modules -> config -> keybinds -> tick + HUD hooks.
 * Everything else lives in {@code core} (framework), {@code setting}, {@code module} and {@code gui}.
 */
public final class MythClient implements ClientModInitializer {

    public static final String ID = "mythclient";
    public static final String NAME = "Myth Client";
    public static final String VERSION = "0.1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static ModuleManager modules;
    private static ConfigManager config;
    private static KeyBinding.Category keyCategory;
    private static KeyBinding guiKey;
    private static KeyBinding hudKey;
    private static KeyBinding waypointKey;
    private static WaypointManager waypoints;
    private static boolean hudVisible = true;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting {} {}", NAME, VERSION);

        // One keybind category ("Myth Client") shown in Options > Controls.
        keyCategory = KeyBinding.Category.create(Identifier.of(ID, "main"));
        guiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.mythclient.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, keyCategory));

        hudKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.mythclient.hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, keyCategory));
        waypointKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.mythclient.waypoint_add", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, keyCategory));

        waypoints = new WaypointManager();
        waypoints.load();

        modules = new ModuleManager(keyCategory);
        modules.registerDefaults();

        config = new ConfigManager(modules);
        config.load();

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        // NOTE: newer Fabric API versions prefer HudElementRegistry; HudRenderCallback still works (deprecated).
        HudRenderCallback.EVENT.register((context, tickCounter) -> modules.renderHud(context));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());
    }

    private void onTick(MinecraftClient client) {
        while (guiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new ClickGuiScreen(null));
            }
        }
        while (hudKey.wasPressed()) {
            hudVisible = !hudVisible;
        }
        while (waypointKey.wasPressed()) {
            if (client.player != null) waypoints.addHere(""); // silent: no on-screen message
        }
        modules.tick(client);
    }

    /** False while the "hide Myth HUD" key has everything hidden. */
    public static boolean hudVisible() {
        return hudVisible;
    }

    public static WaypointManager waypoints() {
        return waypoints;
    }

    public static ModuleManager modules() {
        return modules;
    }

    public static ConfigManager config() {
        return config;
    }
}
